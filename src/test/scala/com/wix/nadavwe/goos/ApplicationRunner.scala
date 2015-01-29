package com.wix.nadavwe.goos

import java.util.concurrent.{ArrayBlockingQueue, TimeUnit}

import com.objogate.wl.swing.AWTEventQueueProber
import com.objogate.wl.swing.driver.ComponentDriver._
import com.objogate.wl.swing.driver.{JFrameDriver, JLabelDriver}
import com.objogate.wl.swing.gesture.GesturePerformer
import com.wix.nadavwe.goos.XmppMatchers._
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.{Chat, ChatManagerListener, MessageListener, XMPPConnection}
import org.specs2.matcher.{Matcher, MustThrownMatchers}

class ApplicationRunner {
  import ApplicationRunner._

  private var driver:AuctionSniperDriver = _

  def startBiddingIn(auction:FakeAuctionServer) {
    val thread = new Thread("Test Application") {
    override def run {
        try {
          Main.main(Constants.XMPPHostname, SNIPER_ID, SNIPER_PASSWORD, auction.itemId)
        } catch {
          case e:Exception => e.printStackTrace()
        }
      }
    }
    thread.setDaemon(true)
    thread.start()
    driver = new AuctionSniperDriver(1000)
    driver.showsSniperStatus(Main.StatusJoining);
  }

  def showsSniperHasLostAuction() {
    driver.showsSniperStatus(Main.StatusLost)
  }

  def hasShownSniperIsBidding() = ???

  def stop() {
    if (driver != null) {
      driver.dispose()
    }
  }
}

object ApplicationRunner {
  private val SNIPER_ID = "sniper"
  private val SNIPER_PASSWORD = "sniper"
  val SNIPER_XMPP_ID = s"$SNIPER_ID@${Constants.XMPPHostname}/${Main.AUCTION_RESOURCE}"
}


class AuctionSniperDriver(timeoutMillis:Int) extends JFrameDriver(
  new GesturePerformer(), JFrameDriver.topLevelFrame(named(Main.MAIN_WINDOW_NAME), showingOnScreen()), new AWTEventQueueProber(timeoutMillis, 100)) {
  import org.hamcrest.Matchers._
  def showsSniperStatus(statusText:String) {
    new JLabelDriver(this, named(Main.SNIPER_STATUS_NAME)).hasText(equalTo(statusText));
  }
}

class FakeAuctionServer(val itemId:String) extends MustThrownMatchers {
  import Main._
  private val AUCTION_PASSWORD = "auction"

  private val connection = new XMPPConnection(Constants.XMPPHostname)
  private var currentChat:Chat = _
  private val messageListener = new SingleMessageListener()

  def startSellingItem() {
    connection.connect()
    connection.login(ITEM_ID_AS_LOGIN.format(itemId), AUCTION_PASSWORD, AUCTION_RESOURCE)
    connection.getChatManager().addChatListener(
    new ChatManagerListener() {
      override def chatCreated(chat:Chat, createdLocally:Boolean) {
        currentChat = chat
        chat.addMessageListener(messageListener)
      }
    })
  }

  def reportPrice(price: Int, increment: Int, bidder: String) = {
    currentChat.sendMessage(s"SOLVersion: 1.1; Event: PRICE; " +
      s"CurrentPrice: $price; Increment: $increment; Bidder: $bidder;")
  }

  def hasReceivedJoinRequestFromSniper() { messageListener.receivesAMessage(anything)  }


  def hasReceivedBid(bid: Int, bidderId: String) = {
    currentChat.getParticipant must beEqualTo(bidderId)

    currentChat must beWithParticipant(bidderId)
    messageListener.receivesAMessage(beEqualTo(s"SOLVersion: 1.1; Command: BID; Price: $bid;"))
  }


  def announceClosed()  { currentChat.sendMessage(new Message())  }
  def stop() { connection.disconnect()  }

}

class SingleMessageListener extends MessageListener with MustThrownMatchers {
  private val messages = new ArrayBlockingQueue[Message](1)
  def processMessage(chat:Chat, message:Message) { messages.add(message) }
  def receivesAMessage(bodyMatcher: Matcher[String]) {
    val message = messages.poll(5, TimeUnit.SECONDS)
    message must not(beNull)
    message must haveBodyThat(bodyMatcher)
    }
}




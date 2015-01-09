package com.wix.nadavwe.goos.e2e

import java.util.concurrent.{TimeUnit, ArrayBlockingQueue}

import com.objogate.wl.swing.AWTEventQueueProber
import com.objogate.wl.swing.driver.{JLabelDriver, JFrameDriver}
import com.objogate.wl.swing.gesture.GesturePerformer
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.{MessageListener, Chat, ChatManagerListener, XMPPConnection}
import com.objogate.wl.swing.driver.ComponentDriver._
import org.hamcrest.Matchers._
import org.specs2.matcher.MustMatchers

import com.wix.nadavwe.goos.e2es.Constants

class ApplicationRunner {
  private val SNIPER_ID = "sniper"
  private val SNIPER_PASSWORD = "sniper"
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

  def stop() {
    if (driver != null) {
      driver.dispose()
    }
  }
}

class AuctionSniperDriver(timeoutMillis:Int) extends JFrameDriver(
  new GesturePerformer(), JFrameDriver.topLevelFrame(named(Main.MAIN_WINDOW_NAME), showingOnScreen()), new AWTEventQueueProber(timeoutMillis, 100)) {

  def showsSniperStatus(statusText:String) {
    new JLabelDriver(this, named(Main.SNIPER_STATUS_NAME)).hasText(equalTo(statusText));
  }
}

class FakeAuctionServer(val itemId:String) {
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

  def hasReceivedJoinRequestFromSniper() { messageListener.receivesAMessage()  }
  def announceClosed()  { currentChat.sendMessage(new Message())  }
  def stop() { connection.disconnect()  }

 }

class SingleMessageListener extends MessageListener with MustMatchers {
  private val messages = new ArrayBlockingQueue[Message](1)
  def processMessage(chat:Chat, message:Message) { messages.add(message) }
  def receivesAMessage() {
    (messages.poll(5, TimeUnit.SECONDS) aka "polled message" must not beNull) orThrow
    }
}



package com.wix.nadavwe.goos.e2e

import com.objogate.wl.swing.AWTEventQueueProber
import com.objogate.wl.swing.driver.{JLabelDriver, JFrameDriver}
import com.objogate.wl.swing.gesture.GesturePerformer
import org.jivesoftware.smack.{Chat, ChatManagerListener, XMPPConnection}
import com.objogate.wl.swing.driver.ComponentDriver._
import org.hamcrest.Matchers._


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
    driver.showsSniperStatus(Constants.StatusJoining);
  }

  def showsSniperHasLostAuction() {
    driver.showsSniperStatus(Constants.StatusLost)
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
  val ITEM_ID_AS_LOGIN = s"auction-%itemId"
  val AUCTION_RESOURCE = "Auction"
  private val AUCTION_PASSWORD = "auction";

  private val connection = new XMPPConnection(Constants.XMPPHostname);
  private var currentChat:Chat = _

  def startSellingItem() {
    connection.connect()
    connection.login(ITEM_ID_AS_LOGIN, AUCTION_PASSWORD, AUCTION_RESOURCE)
    connection.getChatManager().addChatListener(
        new ChatManagerListener() {
          def chatCreated(chat:Chat, createdLocally:Boolean) {
            currentChat = chat; }
    })
  }
 }


object Constants {
  val XMPPHostname = "localhost"

  val StatusJoining = ""
  val StatusLost = ""
}
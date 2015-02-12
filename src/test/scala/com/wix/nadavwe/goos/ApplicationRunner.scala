package com.wix.nadavwe.goos

import com.objogate.wl.swing.AWTEventQueueProber
import com.objogate.wl.swing.driver.ComponentDriver._
import com.objogate.wl.swing.driver.{JFrameDriver, JLabelDriver}
import com.objogate.wl.swing.gesture.GesturePerformer

class ApplicationRunner {

  import com.wix.nadavwe.goos.ApplicationRunner._

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

  def hasShownSniperIsBidding() = driver.showsSniperStatus(Main.StatusBidding)

  def hasShownSniperIsWinning() = driver.showsSniperStatus(Main.StatusWinning)
  def showsSniperHasWonAuction() = driver.showsSniperStatus(Main.StatusWon)

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





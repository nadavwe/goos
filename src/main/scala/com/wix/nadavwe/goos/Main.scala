package com.wix.nadavwe.goos

import java.awt.Color
import java.awt.event.{WindowAdapter, WindowEvent}
import javax.swing.border.LineBorder
import javax.swing.{JFrame, JLabel, SwingUtilities}

import org.jivesoftware.smack.{Chat, XMPPConnection}

class Main(hostname:String, username:String, password:String) extends AuctionEventListener {
  import com.wix.nadavwe.goos.Main._

  private var notToBeGCed : Chat = _
  private var ui : MainWindow = _
  startUserInterface()

  private val connection = connectTo()

  private def startUserInterface() {
    SwingUtilities.invokeAndWait { ui = new MainWindow }
  }

  private def connectTo() : XMPPConnection = {
    val connection = new XMPPConnection(hostname)
    connection.connect()
    connection.login(username, password, AUCTION_RESOURCE)
    connection
  }

  def disconnectWhenUICloses(connection: XMPPConnection) =   ui.addWindowListener(
    onWindowClosed {
      connection.disconnect()
    })

  private def joinAuction(itemId:String)  {
    disconnectWhenUICloses(connection)
    val chat = connection.getChatManager.createChat(
      auctionId(itemId),
      new AuctionMessageTranslator(this)
    )

    notToBeGCed = chat

    chat.sendMessage(JoinCommandFormat)
  }

  def auctionId(itemId:String) = AUCTION_ID_FORMAT.format(itemId, Constants.XMPPHostname)

  override def auctionClosed() = SwingUtilities.invokeLater { ui.showStatus(Main.StatusLost) }

  override def currentPrice(price: Int, increment: Int): Unit = ???
}

class MainWindow extends JFrame("AuctionSniper") {
  import com.wix.nadavwe.goos.Main._

  val sniperStatus = createLabel(StatusJoining)

  setName(MAIN_WINDOW_NAME)
  add(sniperStatus)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setVisible(true)


  private def createLabel(initialText:String) = {
    val result = new JLabel(initialText)
    result.setName(SNIPER_STATUS_NAME)
    result.setBorder(new LineBorder(Color.BLACK))
    result
  }

  def showStatus(status:String): Unit = {
    sniperStatus.setText(status)
  }

}


object Main {

  val ARG_HOSTNAME = 0
  val ARG_USERNAME = 1
  val ARG_PASSWORD = 2
  val ARG_ITEM_ID = 3

  def BidCommandFormat(bid: Int) = s"SOLVersion: 1.1; Command: BID; Price: $bid;"
  val JoinCommandFormat = "SOLVersion: 1.1; Command: JOIN;"


  def main(args:Array[String]): Unit = main(args:_*)
  def main(args: String*): Unit = {
    val mainObj = new Main(args(ARG_HOSTNAME), args(ARG_USERNAME), args(ARG_PASSWORD))
    mainObj.joinAuction(args(ARG_ITEM_ID))
  }

  val MAIN_WINDOW_NAME: String = "Auction Sniper Main"
  val SNIPER_STATUS_NAME: String = "sniper status"

  val StatusJoining = "Joining"
  val StatusLost = "Lost"
  val StatusBidding = "Bidding"

  val ITEM_ID_AS_LOGIN = "auction-%s"
  val AUCTION_RESOURCE = "Auction"
  val AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

  implicit def createRunnable(f: => Unit): Runnable = new Runnable {
    override def run(): Unit = f
  }

  def onWindowClosed(f: => Unit): WindowAdapter = new WindowAdapter {
      override def windowClosed(e: WindowEvent): Unit = f
  }

}

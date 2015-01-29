package com.wix.nadavwe.goos

import java.awt.Color
import javax.swing.border.LineBorder
import javax.swing.{JFrame, JLabel, SwingUtilities}

import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.{Chat, MessageListener, XMPPConnection}

class Main(hostname:String, username:String, password:String) {
  import com.wix.nadavwe.goos.Main._

  private var notToBeGCed : Chat = _
  private var ui : MainWindow = _
  startUserInterface()

  private val connection = connectTo()

  private def startUserInterface() {
    SwingUtilities.invokeAndWait({ui = new MainWindow})
  }

  private def connectTo() : XMPPConnection = {
    val connection = new XMPPConnection(hostname)
    connection.connect()
    connection.login(username, password, AUCTION_RESOURCE)
    connection
  }

  private def joinAuction(itemId:String)  {
    val chat = connection.getChatManager.createChat(
      auctionId(itemId),
      new MessageListener {
        override def processMessage(aChat:Chat, message: Message) = SwingUtilities.invokeLater(ui.showStatus(Main.StatusLost))
      }
    )

    notToBeGCed = chat

    chat.sendMessage(new Message)
  }

  def auctionId(itemId:String) = AUCTION_ID_FORMAT.format(itemId, Constants.XMPPHostname)

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


  def main(args:Array[String]): Unit = main(args:_*)
  def main(args: String*): Unit = {
    val mainObj = new Main(args(ARG_HOSTNAME), args(ARG_USERNAME), args(ARG_PASSWORD))
    mainObj.joinAuction(args(ARG_ITEM_ID))
  }

  val MAIN_WINDOW_NAME: String = "Auction Sniper Main"
  val SNIPER_STATUS_NAME: String = "sniper status"

  val StatusJoining = "Joining"
  val StatusLost = "Lost"

  val ITEM_ID_AS_LOGIN = "auction-%s"
  val AUCTION_RESOURCE = "Auction"
  val AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

  implicit def createRunnable(f: => Unit): Runnable = new Runnable {
    override def run(): Unit = f
  }

}

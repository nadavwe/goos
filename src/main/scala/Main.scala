package com.wix.nadavwe.goos.e2e

import java.awt.Color
import javax.swing.border.LineBorder
import javax.swing.{JLabel, JFrame, SwingUtilities}

import com.wix.nadavwe.goos.e2es.Constants
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.{MessageListener, Chat, XMPPConnection}

class Main(hostname:String, username:String, password:String, itemId:String) {
  import Main._

  private var ui : MainWindow = _
  startUserInterface()
  private val connection = connectTo()
  startXmppConnection()

  private def startUserInterface() {
    SwingUtilities.invokeAndWait(new Runnable {
      override def run() =  {ui = new MainWindow}
    })
  }

  private def connectTo() : XMPPConnection = {
    val connection = new XMPPConnection(hostname)
    connection.connect()
    connection.login(username, password, AUCTION_RESOURCE)
    connection
  }

  private def startXmppConnection()  {
    val chat = connection.getChatManager.createChat(
      auctionId,
      new MessageListener {
        override def processMessage(aChat:Chat, message: Message) {}
      }
    )
    chat.sendMessage(new Message)
  }

  def auctionId = AUCTION_ID_FORMAT.format(itemId, Constants.XMPPHostname)

}

class MainWindow extends JFrame("AuctionSniper") {
  import Main._

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


}


object Main {
  val ARG_HOSTNAME = 0
  val ARG_USERNAME = 1
  val ARG_PASSWORD = 2
  val ARG_ITEM_ID = 3


  def main(args:Array[String]): Unit = main(args:_*)
  def main(args: String*): Unit = { new Main(args(ARG_HOSTNAME), args(ARG_USERNAME), args(ARG_PASSWORD), args(ARG_ITEM_ID)) }

  val MAIN_WINDOW_NAME: String = "Auction Sniper Main"
  val SNIPER_STATUS_NAME: String = "sniper status"

  val StatusJoining = "Joining"
  val StatusLost = ""

  val ITEM_ID_AS_LOGIN = "auction-%s"
  val AUCTION_RESOURCE = "Auction"
  val AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

}

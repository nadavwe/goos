package com.wix.nadavwe.goos

import java.util.concurrent.{ArrayBlockingQueue, TimeUnit}

import com.wix.nadavwe.goos.XmppMatchers._
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.{Chat, ChatManagerListener, MessageListener, XMPPConnection}
import org.specs2.matcher.{Matcher, MustThrownMatchers}

class FakeAuctionServer(val itemId:String) extends MustThrownMatchers {
  import com.wix.nadavwe.goos.Main._
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

  def hasReceivedJoinRequestFrom(bidderId:String) = receivesAMessageMatching(bidderId, beEqualTo(JoinCommandFormat))

  def hasReceivedBid(bid: Int, bidderId: String) = receivesAMessageMatching(bidderId, beEqualTo(BidCommandFormat(bid)))


  def receivesAMessageMatching(bidderId: String, matcher: Matcher[String]) {
    messageListener.receivesAMessage(matcher)
    currentChat must beWithParticipant(bidderId)
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

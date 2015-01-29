package com.wix.nadavwe.goos

import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.{Chat, MessageListener}

trait AuctionEventListener {
  def currentPrice(price: Int, increment: Int)
  def auctionClosed()
}

class AuctionMessageTranslator(listener: AuctionEventListener) extends MessageListener {

  private def unpackEventFrom(message: Message): Map[String, String] =
    (for { elem <- message.getBody.split(";")
            split = elem.split (":") }
          yield split(0).trim -> split(1).trim).toMap


  override def processMessage(chat: Chat, message: Message) = {
    val event: Map[String, String] = unpackEventFrom(message)
    val eventType = event.get("Event")
    eventType match {
      case Some("CLOSE") => listener.auctionClosed()
      case Some("PRICE") => listener.currentPrice(event.get("CurrentPrice").getOrElse("0").toInt, event.get("Increment").getOrElse("0").toInt)
    }
  }
}

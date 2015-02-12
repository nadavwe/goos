package com.wix.nadavwe.goos

import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.{Chat, MessageListener}

trait AuctionEventListener {
  def currentPrice(price: Int, increment: Int)
  def auctionClosed()
}

class AuctionMessageTranslator(listener: AuctionEventListener) extends MessageListener {

  override def processMessage(chat: Chat, message: Message) =
    BidEvent(message.getBody) match {
      case BidClosed => listener.auctionClosed()
      case BidPrice(price, increment) => listener.currentPrice(price, increment)
    }
}

sealed abstract class BidEvent
case object BidClosed extends BidEvent
case class BidPrice(price:Int, increment:Int) extends BidEvent

object BidEvent {
  private def unpackEventFrom(s: String): Map[String, String] =
    (for (elem <- s.split(";"))
          yield elem.split(":").map(_.trim).toTuple
    ).toMap

  private implicit class TwoElementArrayToTuple[T](a:Array[T]) {
    def toTuple = a match {
      case Array(key, value) => key -> value
    }
  }

  private implicit def stringOption2Int(s:Option[String]) = s.map(_.toInt).getOrElse(0)

  def apply(s: String): BidEvent = {
    val event: Map[String, String] = unpackEventFrom(s)
    val eventType = event.get("Event")
    eventType match {
      case Some("CLOSE") => BidClosed
      case Some("PRICE") => BidPrice(event.get("CurrentPrice"), event.get("Increment"))
    }
  }
}


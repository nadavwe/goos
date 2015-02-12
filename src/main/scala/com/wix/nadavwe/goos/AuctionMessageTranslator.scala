package com.wix.nadavwe.goos

import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.{Chat, MessageListener}

sealed trait PriceSource
case object FromSniper extends PriceSource
case object FromOtherBidder extends PriceSource

trait AuctionEventListener {
  def currentPrice(price: Int, increment: Int, priceSource: PriceSource)
  def auctionClosed()
}

class AuctionMessageTranslator(sniperId: String, listener: AuctionEventListener) extends MessageListener {

  override def processMessage(chat: Chat, message: Message) =
    BidEvent(message.getBody) match {
      case BidClosed => listener.auctionClosed()
      case BidPrice(price, increment, `sniperId`) => listener.currentPrice(price, increment, FromSniper)
      case BidPrice(price, increment, _) => listener.currentPrice(price, increment, FromOtherBidder)
    }
}

sealed abstract class BidEvent
case object BidClosed extends BidEvent
case class BidPrice(price: Int, increment: Int, bidder: String) extends BidEvent

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
      case Some("PRICE") => BidPrice(event.get("CurrentPrice"), event.get("Increment"), event.get("Bidder").getOrElse(""))
    }
  }
}


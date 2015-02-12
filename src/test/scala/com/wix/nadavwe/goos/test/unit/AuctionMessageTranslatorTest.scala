package com.wix.nadavwe.goos.test.unit

import com.wix.nadavwe.goos._
import org.jivesoftware.smack.packet.Message
import org.specs2.matcher.Matchers
import org.specs2.mutable.Specification
import com.wixpress.common.specs2.JMock
import org.specs2.specification.Scope

class AuctionMessageTranslatorTest extends Specification with Matchers with JMock {
  val SniperId = "Sniper Id"

  trait Context extends Scope {
    val listener = mock[AuctionEventListener]
    val translator = new AuctionMessageTranslator(SniperId, listener)
    val UnusedChat = null
  }

  "auction message translator" should {
    "notify auction closed when close message received" in new Context {
      checking {
        oneOf(listener).auctionClosed()
      }

      val message = new Message
      message.setBody("SOLVersion: 1.1; Event: CLOSE;")
      translator.processMessage(UnusedChat, message)
      success
    }

    "notify bid details when current price message received from other bidder" in new Context {
      checking {
        exactly(1).of(listener).currentPrice(192, 7, FromOtherBidder)
      }

      val message = new Message
      message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;")
      translator.processMessage(UnusedChat, message)
      success

    }

    "notify bid details when current price message received from other sniper" in new Context {
      checking {
        exactly(1).of(listener).currentPrice(234, 5, FromSniper)
      }

      val message = new Message
      message.setBody(s"SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: ${SniperId};")
      translator.processMessage(UnusedChat, message)
      success

    }

  }


}

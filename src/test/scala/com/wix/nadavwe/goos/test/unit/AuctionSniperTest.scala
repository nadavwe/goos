package com.wix.nadavwe.goos.test.unit

import com.wix.nadavwe.goos.{Auction, AuctionSniper, SniperListener}
import com.wixpress.common.specs2.JMock
import org.specs2.matcher.Matchers
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class AuctionSniperTest extends Specification with Matchers with JMock {

  abstract class Context extends Scope {
    val sniperListener = mock[SniperListener]
    val auction = mock[Auction]
    val sniper = new AuctionSniper(auction, sniperListener)
  }

  "Auction Sniper" should {
    "report lost when auction closes" in new Context {
      checking {
        atLeast(1).of(sniperListener).sniperLost()
      }
      sniper.auctionClosed()
    }

    "bid higher and report bidding when new price arrives" in new Context {
      val price = 1001;
      val increment = 25;
      checking {
        oneOf(auction).bid(price + increment)
        atLeast(1).of(sniperListener).sniperBidding()
      }
      sniper.currentPrice(price, increment)
    }

  }

}

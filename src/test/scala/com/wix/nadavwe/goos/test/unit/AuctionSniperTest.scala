package com.wix.nadavwe.goos.test.unit

import com.wix.nadavwe.goos._
import com.wixpress.common.specs2.JMock
import org.jmock.States
import org.specs2.matcher.Matchers
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class AuctionSniperTest extends Specification with Matchers with JMock {
  abstract class Context extends Scope {
    val sniperState: States = states("sniperState")
    val sniperListener = mock[SniperListener]
    val auction = mock[Auction]
    val sniper = new AuctionSniper(auction, sniperListener)
  }


  "Auction Sniper" should {
    "report lost when auction closes immediately" in new Context {
      checking {
        atLeast(1).of(sniperListener).sniperLost()
      }
      sniper.auctionClosed()
    }

    "report lost when auction closes when bidding" in new Context {
      checking {
        ignoring(auction)
        allowing(sniperListener).sniperBidding(); set(sniperState.is("bidding"))
        atLeast(1).of(sniperListener).sniperLost(); when(sniperState.is("bidding"))

      }
      sniper.currentPrice(123, 45, FromOtherBidder)
      sniper.auctionClosed()
    }

    "report won when auction closes when winning" in new Context {
      checking {
        ignoring(auction)
        allowing(sniperListener).sniperWinning(); set(sniperState.is("winning"))
        atLeast(1).of(sniperListener).sniperWon(); when(sniperState.is("winning"))
      }
      sniper.currentPrice(123, 45, FromSniper)
      sniper.auctionClosed()
    }



    "bid higher and report bidding when new price arrives" in new Context {
      val price = 1001;
      val increment = 25;
      checking {
        oneOf(auction).bid(price + increment)
        atLeast(1).of(sniperListener).sniperBidding()
      }
      sniper.currentPrice(price, increment, FromOtherBidder)
    }

    "report is winning when current price comes from sniper" in new Context {
      checking {
        atLeast(1).of(sniperListener).sniperWinning()
      }
      sniper.currentPrice(123, 45, FromSniper)
    }

  }

}

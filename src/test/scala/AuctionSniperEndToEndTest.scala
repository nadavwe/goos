package com.wix.nadavwe.goos.e2e

import org.specs2.matcher.Matchers
import org.specs2.mutable.{After, Specification}
import org.specs2.specification.Scope


class AuctionSniperEndToEndTest extends Specification with Matchers {

  //sequential

  trait Context extends Scope with After {
    val auction: FakeAuctionServer = new FakeAuctionServer("item-54321")
    val application: ApplicationRunner = new ApplicationRunner()

    def after {
      // Additional cleanup
      auction.stop();
      application.stop();
    }
  }

  "sniper" should {
    "join auction until auction closes" in new Context {
      auction.startSellingItem()
      application.startBiddingIn(auction)
      auction.hasReceivedJoinRequestFromSniper()
      auction.announceClosed()
      application.showsSniperHasLostAuction()
      success
    }
  }


}
package com.wix.nadavwe.goos.e2e

import org.specs2.matcher.Matchers
import org.specs2.mutable.Specification


class AuctionSniperEndToEndTest extends Specification with Matchers {

  sequential

  private val auction: FakeAuctionServer = new FakeAuctionServer("item-54321")
  private val application: ApplicationRunner = new ApplicationRunner()

  "sniper" should {
    "join auction until auction closes" in {
      auction.startSellingItem()
      application.startBiddingIn(auction)
      auction.hasReceivedJoinRequestFromSniper()
      auction.announceClosed()
      application.showsSniperHasLostAuction()
    }


  }

  // Additional cleanup
  step {
    auction.stop();
    application.stop();
  }
}
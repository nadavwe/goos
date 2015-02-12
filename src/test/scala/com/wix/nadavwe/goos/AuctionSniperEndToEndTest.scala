package com.wix.nadavwe.goos

import org.specs2.matcher.Matchers
import org.specs2.mutable.{After, Specification}
import org.specs2.specification.Scope


class AuctionSniperEndToEndTest extends Specification with Matchers {
  sequential

  trait Context extends Scope with After {
    val auction: FakeAuctionServer = new FakeAuctionServer("item-54321")
    val application: ApplicationRunner = new ApplicationRunner()

    def after {
      // Additional cleanup
      auction.stop()
      application.stop()
    }
  }

  "sniper" should {
    "join auction until auction closes" in new Context {
      auction.startSellingItem()
      application.startBiddingIn(auction)
      auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)
      auction.announceClosed()
      application.showsSniperHasLostAuction()
      success
    }

    "make a higher bid and lose" in new Context {
      auction.startSellingItem()

      application.startBiddingIn(auction)
      auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)

      auction.reportPrice(1000, 98, "other bidder")
      application.hasShownSniperIsBidding()

      auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID)

      auction.announceClosed()
      application.showsSniperHasLostAuction()
      success
    }

    "win an auction by bidding higher" in new Context {
      auction.startSellingItem()

      application.startBiddingIn(auction)
      auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID)

      auction.reportPrice(1000, 98, "other bidder")
      application.hasShownSniperIsBidding()

      auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID)
      auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID)
      application.hasShownSniperIsWinning()

      auction.announceClosed()
      application.showsSniperHasWonAuction()
      success
    }




  }


}
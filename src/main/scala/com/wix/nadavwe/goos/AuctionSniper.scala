package com.wix.nadavwe.goos

import java.util.EventListener

trait SniperListener extends EventListener {
  def sniperLost(): Unit
  def sniperBidding(): Unit
  def sniperWinning(): Unit
  def sniperWon(): Unit
}


class AuctionSniper(auction:Auction, listener: SniperListener) extends AuctionEventListener {
  private var isWinning = false;

  def auctionClosed() = {
    if (isWinning) listener.sniperWon()
    else listener.sniperLost()
  }

  override def currentPrice(price: Int, increment: Int, priceSource: PriceSource): Unit = priceSource match {
    case FromSniper =>
      isWinning = true
      listener.sniperWinning()
    case FromOtherBidder =>
      auction.bid(price + increment)
      listener.sniperBidding()
  }
}

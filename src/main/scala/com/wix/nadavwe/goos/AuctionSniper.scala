package com.wix.nadavwe.goos

import java.util.EventListener

trait SniperListener extends EventListener {
  def sniperLost(): Unit
  def sniperBidding(): Unit
}


class AuctionSniper(auction:Auction, listener: SniperListener) extends AuctionEventListener {
  def auctionClosed() = {
    listener.sniperLost()
  }

  override def currentPrice(price: Int, increment: Int): Unit = {
    auction.bid(price + increment)
    listener.sniperBidding()
  }
}

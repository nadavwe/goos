package com.wix.nadavwe.goos

import java.util.EventListener

trait SniperListener extends EventListener {
  def sniperLost(): Unit
}


class AuctionSniper {

}

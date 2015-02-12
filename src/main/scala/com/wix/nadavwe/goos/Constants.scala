package com.wix.nadavwe.goos

object Constants {
  val XMPPHostname = "localhost"

  def BidCommandFormat(bid: Int) = s"SOLVersion: 1.1; Command: BID; Price: $bid;"
  val JoinCommandFormat = "SOLVersion: 1.1; Command: JOIN;"

}

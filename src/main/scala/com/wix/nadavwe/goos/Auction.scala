package com.wix.nadavwe.goos

import org.jivesoftware.smack.Chat
import Constants._


trait Auction {
  def bid(bidValue: Int): Unit
  def join(): Unit
}

class XMPPAuction(chat:Chat) extends Auction {
    def bid(amount: Int) = chat.sendMessage(BidCommandFormat(amount))
    def join() = chat.sendMessage(JoinCommandFormat)
}

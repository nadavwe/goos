package com.wix.nadavwe.goos

import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.{Chat, MessageListener}

trait AuctionEventListener

class AuctionMessageTranslator extends MessageListener {
  override def processMessage(chat: Chat, message: Message): Unit = {

    }

}

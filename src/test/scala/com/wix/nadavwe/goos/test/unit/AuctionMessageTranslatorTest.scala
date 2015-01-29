package com.wix.nadavwe.goos.test.unit

import com.wix.nadavwe.goos.AuctionMessageTranslator
import org.jivesoftware.smack.packet.Message
import org.specs2.matcher.Matchers
import org.specs2.mutable.Specification

class AuctionMessageTranslatorTest extends Specification with Matchers {
  val UnusedChat = null

  "auction message translator" should {
    "notify auction closed when close message received" in {

      

      val translator = new AuctionMessageTranslator
      val message = new Message
      message.setBody("SOLVersion: 1.1; Event: CLOSE;")
      translator.processMessage(UnusedChat, message)
      success
    }
  }


}

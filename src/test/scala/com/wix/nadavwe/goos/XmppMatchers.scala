package com.wix.nadavwe.goos

import org.jivesoftware.smack.Chat
import org.jivesoftware.smack.packet.Message
import org.specs2.matcher.{AlwaysMatcher, Matcher, Matchers}

object XmppMatchers extends Matchers {
  def anything[T] = AlwaysMatcher[T]()

  def haveBodyThat(matcher:Matcher[String]): Matcher[Message] = matcher ^^ { (_:Message).getBody}
  def beWithParticipant(participant: String): Matcher[Chat] = beEqualTo(participant) ^^ {(_:Chat).getParticipant}

}

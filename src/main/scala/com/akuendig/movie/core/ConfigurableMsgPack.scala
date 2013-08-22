package com.akuendig.movie.core

import org.msgpack.{MessagePack, ScalaMessagePackWrapper}
import org.msgpack.conversion.ValueConversions
import org.msgpack.template.ScalaTemplateRegistry


trait ConfigurableMsgPack extends ScalaMessagePackWrapper with ValueConversions {
  val templateRegistry = new ScalaTemplateRegistry()

  object messagePack extends MessagePack(templateRegistry)

}

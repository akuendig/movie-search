package com.akuendig.movie.domain

import org.msgpack.annotation.{Message, Index}
import java.lang.{Integer => Int, Long, Float, Double}
import scala.reflect.runtime.universe._


@Message final case class Group(
  @Index(1) var id: String,
  @Index(2) var name: Option[String] = None
) extends HasTypeTag {
  def this() = this("")

  lazy val getTypeTag: TypeTag[_] = typeTag[Group]
}

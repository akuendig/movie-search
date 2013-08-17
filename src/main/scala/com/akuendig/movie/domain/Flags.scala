package com.akuendig.movie.domain

import org.msgpack.annotation.{Message, Index}
import java.lang.{Integer => Int, Long, Float, Double}
import scala.reflect.runtime.universe._


@Message final case class Flags(
  @Index(1) var english: Option[Boolean] = None
) extends HasTypeTag {
  def this() = this(None)

  lazy val getTypeTag: TypeTag[_] = typeTag[Flags]
}

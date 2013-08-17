package com.akuendig.movie.domain

import org.msgpack.annotation.{Message, Index}
import java.lang.{Integer => Int, Long, Float, Double}
import scala.reflect.runtime.universe._


@Message final case class Size(
  @Index(1) var number: Int = 0,
  @Index(2) var unit: Option[String] = None
) extends HasTypeTag {
  def this() = this(0)

  lazy val getTypeTag: TypeTag[_] = typeTag[Size]
}




















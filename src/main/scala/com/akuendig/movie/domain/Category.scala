package com.akuendig.movie.domain

import org.msgpack.annotation.{Message, Index}
import scala.reflect.runtime.universe._


@Message final case class Category(
  @Index(1) var id: String= "",
  @Index(2) var metaCat: Option[String] = None,
  @Index(3) var subCat: Option[String] = None
) extends HasTypeTag {
  def this() = this("")

  lazy val getTypeTag: TypeTag[_] = typeTag[Category]
}

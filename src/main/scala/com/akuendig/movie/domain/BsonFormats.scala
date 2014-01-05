package com.akuendig.movie.domain

import reactivemongo.bson.Macros

/**
 * Created by adrian on 05/01/14.
 */
trait BsonFormats {
  implicit val _defaults = reactivemongo.bson.DefaultBSONHandlers

  implicit val _category = Macros.handler[Category]
  implicit val _extInfo = Macros.handler[ExtInfo]
  implicit val _group = Macros.handler[Group]
  implicit val _size = Macros.handler[Size]
  implicit val _release = Macros.handler[Release]
}

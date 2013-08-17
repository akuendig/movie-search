package com.akuendig.movie

import scala.language.postfixOps
import scala.language.implicitConversions


package object domain {
  implicit def byteToPrimitive(opt: Option[Byte]): Option[java.lang.Byte] =
    opt.map[java.lang.Byte](i => i)

  implicit def shortToPrimitive(opt: Option[Short]): Option[java.lang.Short] =
    opt.map[java.lang.Short](i => i)

  implicit def intToPrimitive(opt: Option[Int]): Option[java.lang.Integer] =
    opt.map[java.lang.Integer](i => i)

  implicit def longToPrimitive(opt: Option[Long]): Option[java.lang.Long] =
    opt.map[java.lang.Long](i => i)

  implicit def floatToPrimitive(opt: Option[Float]): Option[java.lang.Float] =
    opt.map[java.lang.Float](i => i)

  implicit def doubleToPrimitive(opt: Option[Double]): Option[java.lang.Double] =
    opt.map[java.lang.Double](i => i)
}

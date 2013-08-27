package com.akuendig.movie

import scala.language.postfixOps
import scala.language.implicitConversions
import reactivemongo.bson._
import reactivemongo.bson.BSONDouble
import reactivemongo.bson.BSONLong
import reactivemongo.bson.BSONInteger


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

  object Implicits {
    implicit val intReadWriter = new BSONHandler[BSONInteger, java.lang.Integer] {
      def write(t: Integer): BSONInteger = BSONInteger(t)

      def read(bson: BSONInteger): Integer = bson.value
    }

    implicit val longReadWriter = new BSONHandler[BSONLong, java.lang.Long] {
      def write(t: java.lang.Long): BSONLong = BSONLong(t)

      def read(bson: BSONLong): java.lang.Long = bson.value
    }

    implicit val floatReadWriter = new BSONHandler[BSONDouble, java.lang.Float] {
      def write(t: java.lang.Float): BSONDouble = BSONDouble(t.toDouble)

      def read(bson: BSONDouble): java.lang.Float = bson.value.toFloat
    }

    implicit val sizeReadWriter = Macros.handler[Size]
    implicit val groupReadWriter = Macros.handler[Group]
    implicit val extInfoReadWriter = Macros.handler[ExtInfo]
    implicit val categoryReadWriter = Macros.handler[Category]
    implicit val releaseLikeReaderWriter = Macros.handler[Release]
  }

}

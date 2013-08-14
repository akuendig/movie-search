package com.akuendig.movie.core

import org.specs2.mutable.Specification


class IterableBackedSeqSpec extends Specification {
  val source = Vector(1, 2, 3, 5, 4)
  "IterableBackedSeq" should {
    "return correct values" in {
      val seq = new IterableBackedSeq(source)

      seq.to[Vector].shouldEqual(source)
    }

    "drop correct values" in {
      val seq = new IterableBackedSeq(source)

      seq.drop(2).to[Vector].shouldEqual(source.drop(2))
    }

    "index correctly values" in {
      val seq = new IterableBackedSeq(source)

      seq(4).shouldEqual(4)
      seq(3).shouldEqual(5)
      seq(4).shouldEqual(4)
      seq(2).shouldEqual(3)
      seq(2).shouldEqual(3)
      seq(1).shouldEqual(2)
      seq(0).shouldEqual(1)
    }
  }
}

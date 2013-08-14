package com.akuendig.movie.core


class IterableBackedSeq[A  >: Null](iter: Iterable[A]) extends scala.collection.immutable.Seq[A] {
  private var curIdx: Int = -1
  private var curRelease: A = null
  private var curIter: Iterator[A] = iter.iterator

  lazy val length: Int = iter.size

  def apply(idx: Int): A = {
    if (idx > curIdx) {
      curIter = curIter.drop(idx - curIdx - 1)
      curIdx = idx
      curRelease = curIter.next
    } else {
      curIter = iterator.drop(idx)
      curIdx = idx
      curRelease = curIter.next
    }

    curRelease
  }

  def iterator: Iterator[A] = iter.iterator
}

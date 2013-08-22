package com.akuendig.movie.core


trait Compressed[A] {
  def data: Array[Byte]
  def read: A
  def frozen[U](action: A => U): U = action(read)
}

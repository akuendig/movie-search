package com.akuendig.movie.storage

import scala.concurrent.Future
import com.akuendig.movie.domain.Release


trait MovieStorage {
  def get(skip: Int, take: Int): Future[Traversable[Release]]
  def put(movies: Traversable[Release]): Future[Int]
}

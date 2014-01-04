package com.akuendig.movie.storage

import scala.concurrent.Future
import com.akuendig.movie.domain.Release


trait MovieStorage {
  def getMovies(skip: Int, take: Int): Future[List[Release]]
}

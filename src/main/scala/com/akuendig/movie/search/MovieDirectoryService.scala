package com.akuendig.movie.search

import scala.concurrent.stm.Ref
import com.akuendig.movie.domain.Release


class MovieDirectoryService(movieDirectory: Ref[Map[String, Release]]) {
  def getMovies: Map[String, Release] = movieDirectory.single.get
}

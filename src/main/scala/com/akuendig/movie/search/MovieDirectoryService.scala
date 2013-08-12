package com.akuendig.movie.search

import com.akuendig.movie.search.domain.Release
import scala.concurrent.stm.Ref


class MovieDirectoryService(movieDirectory: Ref[Map[String, Release]]) {
  def getMovies: Map[String, Release] = movieDirectory.single.get
}

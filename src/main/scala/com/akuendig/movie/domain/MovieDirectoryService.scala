package com.akuendig.movie.domain

import scala.concurrent.stm.Ref


class MovieDirectoryService(movieDirectory: Ref[Map[String, Release]]) {
  def getMovies: Map[String, Release] = movieDirectory.single.get
}

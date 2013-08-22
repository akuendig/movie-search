package com.akuendig.movie.domain

import scala.concurrent.stm.Ref


class MovieDirectoryService(movieDirectory: Ref[Map[String, ReleaseLike]]) {
  def getMovies: Map[String, ReleaseLike] = movieDirectory.single.get
}

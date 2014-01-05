package com.akuendig.movie.domain


final case class QuerySceneReleases(
  var year: Int = 0,
  var month: Int = 0,
  var page: Int = 0
)

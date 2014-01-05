package com.akuendig.movie.domain


final case class PagedReleases(
  var page: Int = 0,
  var totalPages: Int = 0,
  var perPage: Int = 0,
  var releases: Set[Release] = Set.empty
)

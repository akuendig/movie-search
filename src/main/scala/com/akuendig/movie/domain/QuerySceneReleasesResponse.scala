package com.akuendig.movie.domain


final case class QuerySceneReleasesResponse(
  var query: QuerySceneReleases = QuerySceneReleases(),
  var result: Option[PagedReleases] = None
)

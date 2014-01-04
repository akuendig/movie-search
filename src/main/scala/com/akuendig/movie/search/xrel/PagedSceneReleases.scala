package com.akuendig.movie.search.xrel


final case class PagedSceneReleases(
  totalCount: Int,
  pagination: Pagination,
  list: Seq[SceneRelease]
)

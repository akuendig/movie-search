package com.akuendig.movie.search.xrel


case class PagedSceneReleases(
  totalCount: Int,
  pagination: Pagination,
  list: Seq[SceneRelease]
)

package com.akuendig.movie.search.xrel


case class PagedP2PReleases(
  totalCount: Int,
  pagination: Pagination,
  list: Seq[P2PRelease]
)

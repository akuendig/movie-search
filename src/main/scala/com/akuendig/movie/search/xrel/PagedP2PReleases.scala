package com.akuendig.movie.search.xrel


final case class PagedP2PReleases(
  totalCount: Int,
  pagination: Pagination,
  list: Seq[P2PRelease]
)

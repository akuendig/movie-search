package com.akuendig.movie.search.domain

case class PagedReleases(
  page: Int,
  totalPages: Int,
  perPage: Int,
  releases: Seq[Release]
)

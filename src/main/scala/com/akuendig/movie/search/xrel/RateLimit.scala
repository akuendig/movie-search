package com.akuendig.movie.search.xrel


case class RateLimit(
  remainingCalls: Int,
  resetTimeU: Long,
  resetTime: String
)

package com.akuendig.movie.search.xrel


final case class RateLimit(
  remainingCalls: Int,
  resetTimeU: Long,
  resetTime: String
)

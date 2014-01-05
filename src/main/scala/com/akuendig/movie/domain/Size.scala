package com.akuendig.movie.domain


final case class Size(
  var number: Int = 0,
  var unit: Option[String] = None
)

package com.akuendig.movie.domain


final case class Group(
  var id: String = "",
  var name: Option[String] = None
)

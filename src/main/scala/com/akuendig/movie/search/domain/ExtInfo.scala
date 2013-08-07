package com.akuendig.movie.search.domain

/**
* Created with IntelliJ IDEA.
* User: adrian
* Date: 07.08.13
* Time: 21:17
* To change this template use File | Settings | File Templates.
*/
case class ExtInfo(
  id: String,
  tpe: String, // actually 'type'
  title: String,
  linkHref: String,
  rating: Option[Float] = None,
  uris: Seq[String] = Seq.empty,
  numRatings: Option[Int] = None
) extends Serializable

package com.akuendig.movie.domain

import java.lang.{Integer => Int, Long, Float}


trait ReleaseLike {
  def id: String

  def dirname: Option[String]

  def linkHref: Option[String]

  def mainLang: Option[String]

  def pubTime: Option[Long]

  def sizeInfo: Option[Size]

  def groupInfo: Option[Group]

  def extInfo: Option[ExtInfo]

  def category: Option[Category]

  def audioType: Option[String]

  def videoType: Option[String]

  def postTime: Option[Long]

  def tvSeason: Option[Int]

  def tvEpisode: Option[Int]

  def numRatings: Option[Int]

  def audioRating: Option[Float]

  def videoRating: Option[Float]
}

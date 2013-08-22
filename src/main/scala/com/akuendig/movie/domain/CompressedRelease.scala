package com.akuendig.movie.domain

import com.akuendig.movie.core.{KnowsSerializer, Compressed}
import java.lang.{Integer => Int, Long, Float}


abstract class CompressedRelease(val data: Array[Byte]) extends ReleaseLike with Compressed[Release] with KnowsSerializer {
  def id: String = read.id

  def dirname: Option[String] = read.dirname

  def linkHref: Option[String] = read.linkHref

  def mainLang: Option[String] = read.mainLang

  def pubTime: Option[Long] = read.pubTime

  def sizeInfo: Option[Size] = read.sizeInfo

  def groupInfo: Option[Group] = read.groupInfo

  def extInfo: Option[ExtInfo] = read.extInfo

  def category: Option[Category] = read.category

  def audioType: Option[String] = read.audioType

  def videoType: Option[String] = read.videoType

  def postTime: Option[Long] = read.postTime

  def tvSeason: Option[Int] = read.tvSeason

  def tvEpisode: Option[Int] = read.tvEpisode

  def numRatings: Option[Int] = read.numRatings

  def audioRating: Option[Float] = read.audioRating

  def videoRating: Option[Float] = read.videoRating

  override def read: Release =
    serializer.fromBinary(data, classOf[Release]).asInstanceOf[Release]
}

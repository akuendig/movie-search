package com.akuendig.movie.domain

import scala.slick.lifted.{TableQuery, Tag}
import java.lang.{Integer => Int, Float}
import scala.Long


package object tables {

  import scala.slick.driver.H2Driver._
  import com.akuendig.movie.domain

  class Size(tag: Tag) extends Table[domain.Size](tag, "Categories") {
    def number = column[Int]("number")

    def unit = column[Option[String]]("unit")

    def * = (number, unit)
  }

  class Category(tag: Tag) extends Table[domain.Category](tag, "Category") {
    def id = column[String]("id", O.PrimaryKey)

    def metaCat = column[Option[String]]("metaCat")

    def subCat = column[Option[String]]("subCat")

    def * = (id, metaCat, subCat)
  }

  class ExtInfo(tag: Tag) extends Table[domain.ExtInfo](tag, "ExtInfo") {
    def id = column[String]("id", O.PrimaryKey)

    def tpe = column[String]("tpe")

    def title = column[String]("title")

    def linkHref = column[Option[String]]("linkHref")

    def uris = column[Set[String]]("uris")

    def numRatings = column[Option[Int]]("numRatings")

    def rating = column[Option[Float]]("rating")

    def * = (id, tpe, title, linkHref, uris, numRatings, rating)
  }

  class Group(tag: Tag) extends Table[domain.Group](tag, "Group") {
    def id = column[String]("id")

    def name = column[Option[String]]("name")

    def * = (id, name)
  }

  class Release(tag: Tag) extends Table[domain.Release](tag, "Release") {
    def id = column[String]("id", O.PrimaryKey)

    def dirname = column[Option[String]]("dirname")

    def linkHref = column[Option[String]]("linkHref")

    def mainLang = column[Option[String]]("mainLang")

    def pubTime = column[Option[Long]]("pubTime")

    def sizeInfo = column[Option[Size]]("sizeInfo")

    def groupInfoId = column[Option[String]]("groupId")
    def groupInfo = foreignKey("FK_Release_Group", groupInfoId, TableQuery[Group])

    def extInfoId = column[Option[String]]("extInfoId")
    def extInfo = foreignKey("FK_Release_ExtInfo", extInfoId, TableQuery[ExtInfo])

    def categoryId = column[Option[String]]("category")
    def category = foreignKey("FK_Release_Category", categoryId, TableQuery[Category])

    def audioType = column[Option[String]]("audioType")

    def videoType = column[Option[String]]("videoType")

    def postTime = column[Option[Long]]("postTime")

    def tvSeason = column[Option[Int]]("tvSeason")

    def tvEpisode = column[Option[Int]]("tvEpisode")

    def numRatings = column[Option[Int]]("numRatings")

    def audioRating = column[Option[Float]]("audioRating")

    def videoRating = column[Option[Float]]("videoRating")

    def * = (id, dirname, linkHref, mainLang, pubTime, sizeInfo, groupInfo, extInfo, category, audioType, videoType, postTime, tvSeason, tvEpisode, numRatings, audioRating, videoRating)
  }

}

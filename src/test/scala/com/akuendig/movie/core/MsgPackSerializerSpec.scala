package com.akuendig.movie.core

import org.specs2.mutable.Specification
import com.akuendig.movie.domain._
import com.akuendig.movie.domain.Size
import com.akuendig.movie.domain.Release
import com.akuendig.movie.domain.PagedReleases
import scala.Some
import com.akuendig.movie.domain.Category
import spray.http.DateTime
import akka.serialization.Serializer
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import org.eligosource.eventsourced.core.SnapshotSaved


class MsgPackSerializerSpec extends Specification {
  "MessagePackSerializer" should {

    def checkSerialisation(serializer: Serializer, originals: Seq[AnyRef]) {
      val serialized = originals.map(serializer.toBinary)
      val deserialized = serialized.map(serializer.fromBinary(_, originals.head.getClass))

      for ((orig, deser) <- (originals, deserialized).zipped) {
        deser.mustEqual(orig)
      }
    }

    val fullSize = Size(number = 12, unit = Some("MB"))

    "de-/serialize a Size" in {
      val serializer = new MsgPackSerializer()
      val values = Seq(
        Size(number = 12),
        fullSize
      )

      checkSerialisation(serializer, values)
    }

    val fullGroup = Group(id = "123", name = Some("group name"))

    "de-/serialize a Group" in {
      val serializer = new MsgPackSerializer()
      val values = Seq(
        Group(id = "123"),
        fullGroup
      )

      checkSerialisation(serializer, values)
    }

    val fullCategory = Category(id = "123", subCat = Some("subCat"), metaCat = Some("metaCat"))

    "de-/serialize a Category" in {
      val serializer = new MsgPackSerializer()
      val values = Seq(
        Category(id = "123"),
        Category(id = "123", subCat = Some("subCat")),
        Category(id = "123", metaCat = Some("metaCat")),
        fullCategory
      )

      checkSerialisation(serializer, values)
    }

    val fullExtInfo = ExtInfo(id = "123", tpe = "MOVIE", title = "RED", uris = Set("http://www.google.com"), linkHref = Some("http://www.google.com"), numRatings = Some(12), rating = Some(8.5f))

    "de-/serialize a ExtInfo" in {
      val serializer = new MsgPackSerializer()
      val values = Seq(
        new ExtInfo(id = "123", tpe = "MOVIE", title = "RED"),
        new ExtInfo(id = "123", tpe = "MOVIE", title = "RED", linkHref = Some("http://www.google.com")),
        new ExtInfo(id = "123", tpe = "MOVIE", title = "RED", uris = Set("http://www.google.com")),
        new ExtInfo(id = "123", tpe = "MOVIE", title = "RED", numRatings = Some(12)),
        new ExtInfo(id = "123", tpe = "MOVIE", title = "RED", rating = Some(8.5f)),
        new ExtInfo(id = "123", tpe = "MOVIE", title = "RED", numRatings = Some(12), rating = Some(8.5f)),
        new ExtInfo(id = "123", tpe = "MOVIE", title = "RED", linkHref = Some("http://www.google.com"), numRatings = Some(12), rating = Some(8.5f)),
        new ExtInfo(id = "123", tpe = "MOVIE", title = "RED", uris = Set("http://www.google.com"), numRatings = Some(12), rating = Some(8.5f)),
        fullExtInfo
      )

      checkSerialisation(serializer, values)
    }

    val fullRelease = Release(
      id = "123",
      dirname = Some("dirname"),
      linkHref = Some("http://www.google.com"),
      mainLang = Some("en"),
      pubTime = Some(DateTime.now.clicks),
      category = Some(fullCategory),
      sizeInfo = Some(fullSize),
      extInfo = Some(fullExtInfo),
      audioType = Some("AC5.1"),
      videoType = Some("BDRip"),
      postTime = Some(DateTime.now.clicks),
      tvSeason = Some(1),
      tvEpisode = Some(2),
      numRatings = Some(32),
      audioRating = Some(9.2f),
      videoRating = Some(5.6f)
    )

    "de-/serialize a Release" in {
      val serializer = new MsgPackSerializer()
      val values = Seq(
        Release(id = "123"),
        Release(id = "123", dirname = Some("dirname")),
        Release(id = "123", linkHref = Some("http://www.google.com")),
        Release(id = "123", mainLang = Some("en")),
        Release(id = "123", pubTime = Some(DateTime.now.clicks)),
        Release(id = "123", category = Some(fullCategory)),
        Release(id = "123", sizeInfo = Some(fullSize)),
        Release(id = "123", extInfo = Some(fullExtInfo)),
        Release(id = "123", audioType = Some("AC5.1")),
        Release(id = "123", videoType = Some("BDRip")),
        Release(id = "123", postTime = Some(DateTime.now.clicks)),
        Release(id = "123", tvSeason = Some(1)),
        Release(id = "123", tvEpisode = Some(2)),
        Release(id = "123", numRatings = Some(32)),
        Release(id = "123", audioRating = Some(9.2f)),
        Release(id = "123", videoRating = Some(5.6f)),
        fullRelease
      )

      checkSerialisation(serializer, values)
    }

    val fullPagedReleases = PagedReleases(
      page = 1, totalPages = 1, perPage = 2, releases = Set(
        fullRelease,
        fullRelease.copy(id = "321")
      )
    )

    "de-/serialize PagedReleases" in {
      val serializer = new MsgPackSerializer()
      val values = Seq(
        PagedReleases(page = 1, totalPages = 1, perPage = 2),
        fullPagedReleases
      )

      checkSerialisation(serializer, values)
    }

    val fullQuerySceneReleases = QuerySceneReleases(year = 2013, month = 2, page = 1)

    "de-/serialize QuerySceneReleases" in {
      val serializer = new MsgPackSerializer()
      val values = Seq(
        fullQuerySceneReleases
      )

      checkSerialisation(serializer, values)
    }

    val fullQuerySceneReleasesResponse = QuerySceneReleasesResponse(
      query = fullQuerySceneReleases,
      result = Some(fullPagedReleases)
    )

    "de-/serialize QuerySceneReleasesResponse" in {
      val serializer = new MsgPackSerializer()
      val values = Seq(
        QuerySceneReleasesResponse(query = fullQuerySceneReleases, result = None),
        fullQuerySceneReleasesResponse
      )

      checkSerialisation(serializer, values)
    }

    val fullMovieDirectorySnapshot = MovieDirectorySnapshot(
      year = 2013,
      month = 2,
      page = 12,
      totalPages = 80,
      releases = Seq(
        fullRelease,
        fullRelease.copy(id = "321")
      )
    )

    "de-/serialize MovieDirectorySnapshot" in {
      val serializer = new MsgPackSerializer()
      val values = Seq(
        fullMovieDirectorySnapshot
      )

      val serialized = values.map(serializer.toBinary)
      val deserialized = serialized
        .map(serializer.fromBinary(_, classOf[MovieDirectorySnapshot]).asInstanceOf[MovieDirectorySnapshot])

      for ((orig, deser) <- (values, deserialized).zipped) {
        forall(deser.releases)(_ must beAnInstanceOf[CompressedRelease])
        deser.copy(releases = deser.releases.map(_.asInstanceOf[CompressedRelease].read)).mustEqual(fullMovieDirectorySnapshot)
      }
    }

    "de-/serialize MovieDirectorySnapshot with serializeSnapshot" in {
      val serializer = new MsgPackSerializer()
      val out = new ByteArrayOutputStream()
      val metadata = SnapshotSaved(1, 2, 3)

      serializer.serializeSnapshot(out, metadata, fullMovieDirectorySnapshot)

      val data = out.toByteArray
      val in = new ByteArrayInputStream(data)
      val deserialized = serializer.deserializeSnapshot(in, metadata).asInstanceOf[MovieDirectorySnapshot]

      forall(deserialized.releases)(_ must beAnInstanceOf[CompressedRelease])
      deserialized.copy(releases = deserialized.releases.map(_.asInstanceOf[CompressedRelease].read)).mustEqual(fullMovieDirectorySnapshot)
    }
  }
}

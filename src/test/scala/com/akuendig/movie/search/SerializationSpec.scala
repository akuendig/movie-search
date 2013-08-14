
package com.akuendig.movie.search

import akka.testkit.TestKit
import org.specs2.mutable.Specification
import scala.concurrent.ExecutionContext
import com.akuendig.movie.search.domain.{MovieDirectorySnapshot, PagedReleases, Release}
import com.akuendig.movie.core.{ScalaBufSerializer, Core}
import akka.actor.ActorSystem
import akka.serialization.SerializationExtension
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import org.eligosource.eventsourced.core.SnapshotSaved

class SerializationSpec extends TestKit(ActorSystem()) with Specification with Core {
  sequential

  import MovieQueryActor._

  implicit val _ec: ExecutionContext = system.dispatcher

  "The protocol buffer serializer" should {
    "Serialize a Release" in {
      val release = Release(id = "123", dirname = Some("Dir Name"))
      val extension = SerializationExtension(system)

      extension.findSerializerFor(release).must(beAnInstanceOf[com.akuendig.movie.core.ScalaBufSerializer])

      val serialized = extension.serialize(release).get

      serialized.mustNotEqual(null)

      val deserialized = extension.deserialize(serialized, release.getClass).get

      deserialized.mustEqual(release)
    }

    "Serialize a PagedRelease" in {
      val release = PagedReleases(
        page = 1,
        totalPages = 1,
        perPage = 1,
        releases = Vector(Release(id = "123", dirname = Some("Dir Name")))
      )
      val extension = SerializationExtension(system)

      extension.findSerializerFor(release).must(beAnInstanceOf[com.akuendig.movie.core.ScalaBufSerializer])

      val serialized = extension.serialize(release).get

      serialized.mustNotEqual(null)

      val deserialized = extension.deserialize(serialized, release.getClass).get

      deserialized.mustEqual(release)
    }

    "Serialize a QuerySceneReleasesResponse" in {
      val response = QuerySceneReleasesResponse(
        query = QuerySceneReleases(
          year = 2013,
          month = 12,
          page = 1
        ),
        result = Some(PagedReleases(
          page = 1,
          totalPages = 1,
          perPage = 1,
          releases = Vector(Release(id = "123", dirname = Some("Dir Name")))
        ))
      )
      val extension = SerializationExtension(system)

      extension.findSerializerFor(response).must(beAnInstanceOf[com.akuendig.movie.core.ScalaBufSerializer])

      val serialized = extension.serialize(response).get

      serialized.mustNotEqual(null)

      val deserialized = extension.deserialize(serialized, response.getClass).get

      deserialized.mustEqual(response)
    }
  }

  "Serialize a MovieDirectorySnapshot" in {
    val state = MovieDirectorySnapshot(
      year = 2013,
      month = 12,
      page = 1,
      totalPages = 1,
      movies = Vector(Release(id = "123", dirname = Some("Dir Name")))
    )

    val out = new ByteArrayOutputStream()
    val serializer = new ScalaBufSerializer()
    val metaData = SnapshotSaved(1, 2, 3)

    serializer.serializeSnapshot(out, metaData, state)

    val data = out.toByteArray
    val deserialized = serializer.deserializeSnapshot(new ByteArrayInputStream(data), metaData)

    deserialized.mustEqual(state)
  }

}

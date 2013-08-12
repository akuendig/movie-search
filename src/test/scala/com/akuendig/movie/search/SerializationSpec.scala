
package com.akuendig.movie.search

import akka.testkit.TestKit
import org.specs2.mutable.Specification
import scala.concurrent.ExecutionContext
import com.akuendig.movie.search.domain.{PagedReleases, Release}
import com.akuendig.movie.core.Core
import akka.actor.ActorSystem
import akka.serialization.SerializationExtension

class SerializationSpec extends TestKit(ActorSystem()) with Specification with Core {
  sequential

  import MovieQueryActor._

  implicit val _ec: ExecutionContext = system.dispatcher

  "The protocol buffer serializer" should {
    "Serialize a Release" in {
      val release = Release(id = "123", dirname = Some("Dir Name"))
      val extension = SerializationExtension(system)

      extension.findSerializerFor(release).must(beAnInstanceOf[com.akuendig.movie.core.ScalaBufSerializer]).orThrow

      val serialized = extension.serialize(release).get

      serialized.mustNotEqual(null).orThrow

      val deserialized = extension.deserialize(serialized, release.getClass).get

      deserialized.mustEqual(release).orThrow
    }

    "Serialize a PagedRelease" in {
      val release = PagedReleases(
        page = 1,
        totalPages = 1,
        perPage = 1,
        releases = Vector(Release(id = "123", dirname = Some("Dir Name")))
      )
      val extension = SerializationExtension(system)

      extension.findSerializerFor(release).must(beAnInstanceOf[com.akuendig.movie.core.ScalaBufSerializer]).orThrow

      val serialized = extension.serialize(release).get

      serialized.mustNotEqual(null).orThrow

      val deserialized = extension.deserialize(serialized, release.getClass).get

      deserialized.mustEqual(release).orThrow
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

      extension.findSerializerFor(response).must(beAnInstanceOf[com.akuendig.movie.core.ScalaBufSerializer]).orThrow

      val serialized = extension.serialize(response).get

      serialized.mustNotEqual(null).orThrow

      val deserialized = extension.deserialize(serialized, response.getClass).get

      deserialized.mustEqual(response).orThrow
    }
  }

}

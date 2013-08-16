package com.akuendig.movie.core

import org.specs2.mutable.Specification
import com.akuendig.movie.domain.{Size, Release, PagedReleases}


class MsgPackSerializerSpec extends Specification {
  "MessagePackSerializer" should {
    "de-/serialize a Size with unit" in {
      val size = new Size(12, Some("MB"))
      val serializer = new MsgPackSerializer()

      val binary = serializer.toBinary(size)

      binary.size must be greaterThan(0)

      val deserialized = serializer.fromBinary(binary, size.getClass)

      deserialized.mustEqual(size)
    }

    "de-/serialize a Size without unit" in {
      val size = new Size(12, None)
      val serializer = new MsgPackSerializer()

      val binary = serializer.toBinary(size)

      binary.size must be greaterThan(0)

      val deserialized = serializer.fromBinary(binary, size.getClass)

      deserialized.mustEqual(size)
    }

    "de-/serialize a Release" in {
      val release = new Release(id = "123", dirname = Some("Dir Name"))
      val serializer = new MsgPackSerializer()

      val binary = serializer.toBinary(release)

      binary.size must be greaterThan(0)

      val deserialized = serializer.fromBinary(binary, release.getClass)

      deserialized.mustEqual(release)
    }

    "de-/serialize PagedReleases" in {
      val paged = new PagedReleases(
        page = 1,
        totalPages = 1,
        perPage = 1,
        releases = Vector(new Release(id = "123", dirname = Some("Dir Name")))
      )
      val serializer = new MsgPackSerializer()

      val binary = serializer.toBinary(paged)

      binary.size must be greaterThan(0)

      val deserialized = serializer.fromBinary(binary, paged.getClass)

      deserialized.mustEqual(paged)

    }
  }
}

package com.akuendig.movie.domain.template

import org.msgpack.packer.Packer
import org.msgpack.template.Template
import org.msgpack.unpacker.Unpacker
import org.msgpack.`type`.ValueType
import com.akuendig.movie.domain.{CompressedRelease, ReleaseLike, Release}
import org.msgpack.MessagePack
import com.akuendig.movie.core.KnowsSerializer
import akka.serialization.Serializer


abstract class ReleaseLikeTemplate(releaseTemplate: Template[Release], messagePack: MessagePack) extends Template[ReleaseLike] with KnowsSerializer {

  private object ReleaseType extends Enumeration {
    type ReleaseType = Value
    val UNKNOWN, NORMAL, COMPRESSED = Value
  }

  def write(pk: Packer, v: ReleaseLike) {
    write(pk, v, true)
  }

  def write(pk: Packer, v: ReleaseLike, required: Boolean) {
    pk.writeArrayBegin(2)

    v match {
      case compressed: CompressedRelease =>
        pk.write(ReleaseType.COMPRESSED.id)
        pk.write(compressed.data)
      case release: Release =>
        pk.write(ReleaseType.NORMAL.id)
        releaseTemplate.write(pk, release, required)
      case _ =>
        throw new IllegalArgumentException("Unrecognized type of ReleaseLike %s".format(v))
    }

    pk.writeArrayEnd(true)
    pk.flush()
  }

  private val _serializer = serializer

  def read(u: Unpacker, to: ReleaseLike): ReleaseLike = read(u, to, true)

  def read(u: Unpacker, to: ReleaseLike, required: Boolean): ReleaseLike = {
    def readCompressed() = {
      val buffer = messagePack.createBufferPacker()
      buffer.write(releaseTemplate.read(u, null, required))
      buffer.flush()

      new CompressedRelease(buffer.toByteArray) {
        def serializer: Serializer = _serializer
      }
    }

    val arraySize = u.readArrayBegin()

    if (arraySize == 2 && u.getNextType == ValueType.INTEGER) {
      val release = ReleaseType(u.readByte) match {
        case ReleaseType.COMPRESSED =>
          new CompressedRelease(u.readByteArray()) {
            def serializer: Serializer = _serializer
          }
        case ReleaseType.NORMAL =>
          readCompressed()
        case b =>
          throw new IllegalArgumentException("Unrecognized type '%s' of ReleaseLike %s".format(b, to))
      }

      u.readArrayEnd()

      release
    } else {
      readCompressed()
    }
  }
}

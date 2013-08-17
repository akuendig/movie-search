package com.akuendig.movie.core

import akka.serialization.Serializer
import org.eligosource.eventsourced.journal.common.serialization.SnapshotSerializer
import java.io._
import org.eligosource.eventsourced.core.SnapshotMetadata
import org.msgpack.ScalaMessagePack._
import scala.Some


class MsgPackSerializer extends Serializer with SnapshotSerializer {
  val identifier: Int = 294572

  val includeManifest: Boolean = true

  def toBinary(o: AnyRef): Array[Byte] =
    write(o)

  def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = manifest match {
    case Some(clazz) =>
      val template = messagePack.lookup(clazz)
      val obj = messagePack.read(bytes, template)

      obj.asInstanceOf[AnyRef]
    case None => throw new IllegalArgumentException("Need a manifest to be able to deserialize bytes using msgpack")
  }

  def serializeSnapshot(stream: OutputStream, metadata: SnapshotMetadata, state: Any) {
    val packer = messagePack.createPacker(stream)

    packer.write(state.getClass.getName)
    packer.write(state)
    packer.flush()
  }

  def deserializeSnapshot(stream: InputStream, metadata: SnapshotMetadata): Any = {
    val unpacker = messagePack.createUnpacker(stream)

    val className = unpacker.readString()
    val clazz = Class.forName(className)

    unpacker.read(clazz)
  }

}

package com.akuendig.movie.core

import java.io._
import scala.Some
import akka.serialization.Serializer
import com.akuendig.movie.domain.{ReleaseLike, Release}
import org.eligosource.eventsourced.core.SnapshotMetadata
import org.eligosource.eventsourced.journal.common.serialization.SnapshotSerializer
import org.msgpack.template._
import com.akuendig.movie.core.template.{SeqTemplate, GenericIterableTemplate, GenericSeqTemplate, IterableTemplate}
import com.akuendig.movie.domain.template.ReleaseLikeTemplate


class MsgPackSerializer extends ConfigurableMsgPack with Serializer with SnapshotSerializer {
  val identifier: Int = 294572

  val includeManifest: Boolean = true

  {
    val at = new AnyTemplate(templateRegistry)
    def anyTemplate[T] = at.asInstanceOf[T]

    templateRegistry.register(classOf[Seq[Any]], new SeqTemplate(anyTemplate))
    templateRegistry.register(classOf[scala.collection.Seq[Any]], new SeqTemplate(anyTemplate))
    templateRegistry.register(classOf[Iterable[Any]], new IterableTemplate(anyTemplate))
    templateRegistry.register(classOf[scala.collection.Iterable[Any]], new IterableTemplate(anyTemplate))

    templateRegistry.registerGeneric(classOf[Seq[_]], new GenericSeqTemplate())
    templateRegistry.registerGeneric(classOf[scala.collection.Seq[_]], new GenericSeqTemplate())
    templateRegistry.registerGeneric(classOf[Iterable[_]], new GenericIterableTemplate())
    templateRegistry.registerGeneric(classOf[scala.collection.Iterable[_]], new GenericIterableTemplate())

    val _this = this
    templateRegistry.register(classOf[ReleaseLike], new ReleaseLikeTemplate(messagePack.lookup(classOf[Release]), messagePack) {
      def serializer: Serializer = _this
    })
  }

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

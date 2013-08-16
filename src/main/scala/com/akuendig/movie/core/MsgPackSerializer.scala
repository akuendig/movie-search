package com.akuendig.movie.core

import akka.serialization.Serializer
import org.eligosource.eventsourced.journal.common.serialization.SnapshotSerializer
import java.io._
import org.eligosource.eventsourced.core.SnapshotMetadata
import org.msgpack.ScalaMessagePack._
import scala.reflect.runtime.universe._
import com.akuendig.movie.domain.HasTypeTag
import scala.Some
import scala.reflect.runtime.{currentMirror => cm}


class MsgPackSerializer extends Serializer with SnapshotSerializer {
  val identifier: Int = 294572

  val includeManifest: Boolean = true

  def toBinary(o: AnyRef): Array[Byte] = //o match {
  //    case obj: HasTypeTag =>
  //      val stream = new ByteArrayOutputStream()
  //      val out = new ObjectOutputStream(stream)
  //
  //      out.writeBoolean(true)
  //      out.flush()
  //
  //      messagePack.write(out, obj, messagePack.lookup(o.getClass.asInstanceOf[Class[AnyRef]]))
  //
  //      stream.toByteArray
  //    case _ =>
    write(o)

  //  }

  def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = manifest match {
    case Some(clazz) =>
      val dummy = clazz.newInstance()
      val classMirror = cm.classSymbol(clazz)

      val hasTT = dummy.isInstanceOf[HasTypeTag]
      //
      //      val stream = new ByteArrayInputStream(bytes)
      //      val in = new ObjectInputStream(stream)
      //
      //      val hasTT = in.readBoolean()

      if (hasTT) {
        val instanceMirror = cm.reflect(dummy)
        val getTypeTagMethod = classMirror.toType.member(newTermName("getTypeTag")).asMethod
        val getTypeTag = instanceMirror.reflectMethod(getTypeTagMethod)

        val tt = getTypeTag()
        val mf = Manifest.classType(clazz)

        readTo(bytes, dummy).asInstanceOf[AnyRef]
      } else {
        //          messagePack.read(bytes, messagePack.lookup(clazz)).asInstanceOf[AnyRef]
        null
      }
    //      }

    //      reading.now
    //      readAsValue(bytes).as(Manifest.classType(clazz))
    //      read(bytes)(Manifest.classType(clazz)).asInstanceOf[AnyRef]
    //      readTo(bytes, clazz.newInstance).asInstanceOf[AnyRef]
    case None => throw new IllegalArgumentException("Need a manifest to be able to deserialize bytes using msgpack")
  }

  def serializeSnapshot(stream: OutputStream, metadata: SnapshotMetadata, state: Any) {
    val serialized = pack(state)

    //    for (out <- managed(new ObjectOutputStream(stream))) {
    //      out.writeInt(serialized.size)
    //    }

    stream.write(serialized)
  }

  def deserializeSnapshot(stream: InputStream, metadata: SnapshotMetadata): Any = {
    //    val size = {
    //      for (in <- managed(new ObjectInputStream(stream))) yield in.readInt()
    //    }.now
    //
    //    val data = new Array[Byte](size)
    //
    //    stream.read(data, 0, size)

    readAsValue(stream)
  }

}

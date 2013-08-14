package com.akuendig.movie.core

import akka.serialization.Serializer
import com.google.protobuf.MessageLite
import com.google.protobuf.MessageLite.Builder
import org.eligosource.eventsourced.journal.common.serialization.SnapshotSerializer
import java.io.{ObjectInputStream, InputStream, ObjectOutputStream, OutputStream}
import org.eligosource.eventsourced.core.SnapshotMetadata


class ScalaBufSerializer extends Serializer with SnapshotSerializer {
  val ARRAY_OF_BYTE_ARRAY = Array[Class[_]](classOf[Array[Byte]])
  def includeManifest: Boolean = true
  def identifier = 2

  def toBinary(obj: AnyRef): Array[Byte] = obj match {
    case m: MessageLite => m.toByteArray
    case _              => throw new IllegalArgumentException("Can't serialize a non-protobuf message using protobuf [" + obj + "]")
  }

  def fromBinary(bytes: Array[Byte], clazz: Option[Class[_]]): AnyRef =
    clazz match {
      case None    => throw new IllegalArgumentException("Need a protobuf message class to be able to serialize bytes using protobuf")
      case Some(c) =>
        val companion = CompanionHelper.companion(c)
        val companionClazz = companion.getClass
        val builder: Builder = companionClazz.getDeclaredMethod("newBuilder").invoke(companion).asInstanceOf[Builder]

        builder.mergeFrom(bytes)
    }

  def serializeSnapshot(stream: OutputStream, metadata: SnapshotMetadata, state: Any) {
    val objectOut = new ObjectOutputStream(stream)

    state match {
      case m: MessageLite =>
        objectOut.writeBoolean(true)
        objectOut.writeUTF(m.getClass.getName)
        objectOut.writeInt(m.getSerializedSize)
        objectOut.write(toBinary(m))
      case _              =>
        objectOut.writeBoolean(false)
        SnapshotSerializer.java.serializeSnapshot(objectOut, metadata, state)
    }

    objectOut.flush()
  }

  def deserializeSnapshot(stream: InputStream, metadata: SnapshotMetadata): Any = {
    val objectIn = new ObjectInputStream(stream)
    val isProtobuf = objectIn.readBoolean

    if (isProtobuf) {
      val className = objectIn.readUTF()
      val size = objectIn.readInt
      val data = Array.ofDim[Byte](size)
      val clazz = Class.forName(className)

      objectIn.read(data, 0, size)
      fromBinary(data, clazz)
    } else {
      SnapshotSerializer.java.deserializeSnapshot(objectIn, metadata)
    }
  }
}

object CompanionHelper {

  def companion(clazz: Class[_]): AnyRef = {
    // runtime reflection is typically done
    // by importing things from scala.reflect.runtime package
    import scala.reflect.runtime._

    // the new Scala reflection API is mirror based
    // mirrors constitute a hierarchy of objects
    // that closely follows the hierarchy of the things they reflect
    // for example, for a class you'll have a ClassMirror
    // for a method you'll have a MethodMirror and so on
    // why go the extra mile?
    // because this provides more flexibility than traditional approaches
    // you can read more about mirror-based designs here:
    // https://dl.dropbox.com/u/10497693/Library/Computer%20Science/Metaprogramming/Reflection/mirrors.pdf
    // https://dl.dropbox.com/u/10497693/Library/Computer%20Science/Metaprogramming/Reflection/reflecting-scala.pdf

    // bottom line is that to do anything you will need a mirror
    // for example, in your case, you need a ClassMirror

    // remember I said that mirrors provide more flexibility?
    // for one, this means that mirror-based reflection facilities
    // might have multiple implementations
    // in a paper linked above, Gilad Bracha muses over a runtime
    // that loads things remotely over the network
    // in our case we might have different mirrors for JVM and CLR
    // well, anyways

    // the canonical (and the only one now) implementation of the mirror API
    // is Java-based reflection that uses out of the box classloaders
    // here's its root: https://github.com/scalamacros/kepler/blob/9f71e9f114c10b52350c6c4ec757159f06e55daa/src/reflect/scala/reflect/api/Mirrors.scala#L178
    // yeah, right, I've just linked a source file from trunk
    // we'll have Scaladocs for that soon, but for now take a look
    // this file is interfaces-only and is heavy on comments

    // to start with Java-based reflection implementation you need a classloader
    // let's grab one and instantiate the root mirror
    // btw, the same effect could be achieved by writing
    // `scala.reflect.runtime.currentMirror`
    val rootMirror = universe.runtimeMirror(clazz.getClassLoader)

    // now when we've finally entered the reflective world
    // we can get the stuff done
    // first we obtain a ClassSymbol that corresponds to the current instance
    // (ClassSymbols are to Scala the same as Classes are to Java)
    val classSymbol = rootMirror.classSymbol(clazz)

    // having a Scala reflection entity
    // we can obtain its reflection using the rootMirror
    val moduleSymbol = classSymbol.companionSymbol.asModule

    // now we just traverse the conceptual hierarchy of mirrors
    // that closely follows the hierarchy of Scala reflection concepts
    // for example, a ClassMirror has a companion ModuleMirror and vice versa
    val moduleMirror = rootMirror.reflectModule(moduleSymbol)

    // finally, we've arrived at our destination
    moduleMirror.instance.asInstanceOf[AnyRef]
  }
}

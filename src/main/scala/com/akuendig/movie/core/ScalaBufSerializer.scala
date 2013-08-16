package com.akuendig.movie.core

import akka.serialization.Serializer
import com.google.protobuf.{ExtensionRegistryLite, CodedInputStream, CodedOutputStream, MessageLite}
import com.google.protobuf.MessageLite.Builder
import org.eligosource.eventsourced.journal.common.serialization.SnapshotSerializer
import java.io.{InputStream, OutputStream}
import org.eligosource.eventsourced.core.SnapshotMetadata
import resource._
import com.akuendig.movie.search.domain.MovieDirectorySnapshot


class ScalaBufSerializer extends Serializer with SnapshotSerializer {
  val ARRAY_OF_BYTE_ARRAY = Array[Class[_]](classOf[Array[Byte]])

  def includeManifest: Boolean = true

  def identifier = 2

  def toBinary(obj: AnyRef): Array[Byte] = obj match {
    case m: MessageLite => m.toByteArray
    case _ => throw new IllegalArgumentException("Can't serialize a non-protobuf message using protobuf [" + obj + "]")
  }

  def fromBinary(bytes: Array[Byte], clazz: Option[Class[_]]): AnyRef =
    clazz match {
      case None => throw new IllegalArgumentException("Need a protobuf message class to be able to serialize bytes using protobuf")
      case Some(c) =>
        val companion = CompanionHelper.companion(c)
        val companionClazz = companion.getClass
        val builder: Builder = companionClazz.getDeclaredMethod("newBuilder").invoke(companion).asInstanceOf[Builder]

        builder.mergeFrom(bytes)
    }

  def serializeSnapshot(stream: OutputStream, metadata: SnapshotMetadata, state: Any) {
    state match {
      case m: MessageLite => for (out <- managed(CodedOutputStream.newInstance(stream))) {
        out.writeBoolNoTag(true)
        out.writeStringNoTag(m.getClass.getName)
        out.writeInt32NoTag(m.getSerializedSize)
        out.writeMessageNoTag(m)
      }
      case _ =>
        for (out <- managed(CodedOutputStream.newInstance(stream))) {
          out.writeBoolNoTag(false)
        }

        SnapshotSerializer.java.serializeSnapshot(stream, metadata, state)
        stream.flush()
    }
  }

  def deserializeSnapshot(stream: InputStream, metadata: SnapshotMetadata): Any = {
    val in = CodedInputStream.newInstance(stream)
    val isProtobuf = in.readBool()

    try {
      if (isProtobuf) {
        val className = in.readString()
        val size = in.readInt32()

        val clazz = Class.forName(className)
        val companion = CompanionHelper.companion(clazz)
        val companionClazz = companion.getClass
        val builder: Builder = companionClazz.getDeclaredMethod("newBuilder").invoke(companion).asInstanceOf[Builder]

        println(isProtobuf, className, clazz, companion, builder, size)

        in.setSizeLimit(size + in.getTotalBytesRead + 1)

        val message = in.readMessage(builder, ExtensionRegistryLite.getEmptyRegistry)

        println(in.getTotalBytesRead, message.asInstanceOf[MovieDirectorySnapshot])
        message
      } else {
        SnapshotSerializer.java.deserializeSnapshot(stream, metadata)
      }
    } catch {
      case t: Throwable => println(t); throw t
    }
  }

  implicit val codedResource = new Resource[CodedOutputStream] {
    def close(r: CodedOutputStream) {
      r.flush()
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

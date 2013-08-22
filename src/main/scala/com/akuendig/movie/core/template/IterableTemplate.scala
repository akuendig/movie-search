package com.akuendig.movie.core.template

import org.msgpack.MessageTypeException
import org.msgpack.packer.Packer
import org.msgpack.template.{AbstractTemplate, Template}
import org.msgpack.unpacker.Unpacker


class IterableTemplate[T](elementTemplate: Template[T]) extends AbstractTemplate[Iterable[T]] {
  def read(u: Unpacker, to: Iterable[T], required: Boolean): Iterable[T] = {
    if (!required && u.trySkipNil) {
      return null.asInstanceOf[Iterable[T]]
    }

    val length = u.readArrayBegin()
    val builder = Vector.newBuilder[T]

    builder.sizeHint(length)

    for (_ <- 0 until length) {
      builder += elementTemplate.read(u, null.asInstanceOf[T], required)
    }

    u.readArrayEnd()

    builder.result()
  }

  def write(packer: Packer, v: Iterable[T], required: Boolean): Unit = {
    if (v == null) {
      if (required) {
        throw new MessageTypeException("Attempted to write null")
      }
      packer.writeNil()
      return
    }

    packer.writeArrayBegin(v.size)
    v.foreach(e => {
      elementTemplate.write(packer, e, required)
    })
    packer.writeArrayEnd()
  }
}

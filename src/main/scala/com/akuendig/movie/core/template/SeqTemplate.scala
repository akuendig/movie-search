package com.akuendig.movie.core.template

import org.msgpack.MessageTypeException
import org.msgpack.packer.Packer
import org.msgpack.template.{AbstractTemplate, Template}
import org.msgpack.unpacker.Unpacker


class SeqTemplate[T](elementTemplate: Template[T]) extends AbstractTemplate[Seq[T]] {
  def read(u: Unpacker, to: Seq[T], required: Boolean): Seq[T] = {
    if (!required && u.trySkipNil) {
      return null.asInstanceOf[Seq[T]]
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

  def write(packer: Packer, v: Seq[T], required: Boolean) {
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

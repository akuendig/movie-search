package com.akuendig.movie.core.template

import org.msgpack.template.{Template, GenericTemplate}


class GenericIterableTemplate extends GenericTemplate {
  def build(params: Array[Template[_]]) = {
    new IterableTemplate[Any](params(0).asInstanceOf[Template[Any]])
  }
}

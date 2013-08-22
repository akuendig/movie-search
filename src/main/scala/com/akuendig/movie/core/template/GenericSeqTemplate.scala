package com.akuendig.movie.core.template

import org.msgpack.template.{Template, GenericTemplate}


class GenericSeqTemplate extends GenericTemplate {
  def build(params: Array[Template[_]]) = {
    new SeqTemplate[Any](params(0).asInstanceOf[Template[Any]])
  }
}

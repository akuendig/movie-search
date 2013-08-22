package com.akuendig.movie.core

import akka.serialization.Serializer


trait KnowsSerializer {
  def serializer: Serializer
}

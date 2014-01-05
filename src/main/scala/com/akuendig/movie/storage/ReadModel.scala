package com.akuendig.movie.storage

import com.akuendig.movie.domain.Release


object ReadModel {

  sealed trait ReadModelMessage

  final case class StoreReleases(release: Traversable[Release]) extends ReadModelMessage

}

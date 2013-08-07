package com.akuendig.movie.search

import com.akuendig.movie.search.domain.Release


class ReleaseDirectory {
  private var data = Map.empty[String, Release]

  def put(release: Release) {
    data += release.id -> release
  }

  def putAll(releases: Traversable[Release]) {
    releases.foreach(put)
  }

  def all = data
}

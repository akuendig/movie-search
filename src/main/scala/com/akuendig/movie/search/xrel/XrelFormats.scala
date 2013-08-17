package com.akuendig.movie.search.xrel

import org.json4s.DefaultFormats
import com.akuendig.movie.domain._


object XrelFormats extends DefaultFormats {
  withCompanions(
    classOf[SceneRelease] -> this,
    classOf[Flags] -> this,
    classOf[ExtInfo] -> this,
    classOf[Size] -> this,
    classOf[SceneRelease] -> this,
    classOf[DetailedSceneRelease] -> this,
    classOf[Category] -> this,
    classOf[Group] -> this,
    classOf[P2PRelease] -> this,
    classOf[DetailedP2PRelease] -> this,
    classOf[RateLimit] -> this,
    classOf[Pagination] -> this,
    classOf[PagedSceneReleases] -> this,
    classOf[PagedP2PReleases] -> this
  )
}

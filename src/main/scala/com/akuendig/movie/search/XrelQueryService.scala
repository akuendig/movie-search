package com.akuendig.movie.search

import scala.concurrent.Future
import com.akuendig.movie.search.xrel._
import com.akuendig.movie.search.domain.Category


trait XrelQueryService {
  def fetchSceneRelease(page: Int, year: Int, month: Int): Future[PagedSceneReleases]

  def fetchDetailedSceneRelease(id: String): Future[DetailedSceneRelease]

  def fetchP2PCategories(): Future[Seq[Category]]

  def fetchP2PRelease(page: Int, catId: String): Future[PagedP2PReleases]

  def fetchDetailedP2PRelease(id: String): Future[DetailedP2PRelease]

  def fetchRateLimit(): Future[RateLimit]
}

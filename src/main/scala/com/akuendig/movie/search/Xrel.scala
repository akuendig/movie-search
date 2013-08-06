package com.akuendig.movie.search

import java.util.Date
import spray.json.{JsValue, JsObject, RootJsonFormat, DefaultJsonProtocol}

case class Flags()

case class ExtInfo(
  id: String,
  tpe: String, // actually 'type'
  title: String,
  linkHref: String,
  rating: Float
  // Uris       []String
  // NumRatings int
)

case class Size(
  number: Int,
  unit: String
)

case class SceneRelease(
  id: String,
  dirname: String,
  linkHref: String,

  audioType: String,
  videoType: String,

  groupName: String,
  time: Long,

  size: Size,

  extInfo: ExtInfo,

  flags: Flags
)


case class DetailedSceneRelease(
  id: String,
  dirname: String,
  linkHref: String,

  audioType: String,
  videoType: String,

  groupName: String,
  time: Long,

  size: Size,

  extInfo: ExtInfo,

  flags: Flags,

  videoRating: Float,
  audioRating: Float,
  numRatings: Int
)


case class Category(
  id: String,
  metaCat: String,
  subCat: String
)

case class Group(
  Id: String,
  Name: String
)

case class P2PRelease(
  id: String,
  dirname: String,
  linkHref: String,

  category: Category,

  mainLang: String,
  pubTime: Long,
  sizeMB: Int,

  group: Group,

  extInfo: ExtInfo
)

case class DetailedP2PRelease(
  id: String,
  dirname: String,
  linkHref: String,

  category: Category,

  mainLang: String,
  pubTime: Long,
  sizeMB: Int,

  group: Group,

  extInfo: ExtInfo,

  postTime: Long,
  numRatings: Int,
  tvSeason: Int,
  tvEpisode: Int
)

case class RateLimit(
  remainingCalls: Int,
  resetTimeU: Long,
  resetTime: String
)

object MyJsonProtocol extends DefaultJsonProtocol {

  implicit val flagsFormat = new RootJsonFormat[Flags] {
    def write(f: Flags) = JsObject()

    def read(value: JsValue) = Flags()
  }

  implicit val extInfoFormat = jsonFormat5(ExtInfo)
  implicit val sizeFormat = jsonFormat2(Size)
  implicit val sceneReleaseFormat = jsonFormat10(SceneRelease)
  implicit val detailedSceneReleaseFormat = jsonFormat13(DetailedSceneRelease)
  implicit val categoryFormat = jsonFormat3(Category)
  implicit val groupFormat = jsonFormat2(Group)
  implicit val p2pRelease = jsonFormat9(P2PRelease)
  implicit val detailedP2pRelease = jsonFormat13(DetailedP2PRelease)
  implicit val rateLimitFormat = jsonFormat3(RateLimit)

}

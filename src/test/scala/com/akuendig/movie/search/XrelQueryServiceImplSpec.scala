package com.akuendig.movie.search

import akka.testkit.TestKit
import org.specs2.mutable.Specification
import scala.concurrent.{ExecutionContext, Await, Future}
import spray.http._
import java.io.{ByteArrayOutputStream, ObjectOutputStream}
import com.akuendig.movie.search.xrel.SceneRelease
import com.akuendig.movie.domain.{Release, PagedReleases}
import com.akuendig.movie.core.Core
import akka.actor.ActorSystem
import spray.http.MediaTypes._
import akka.util.Timeout
import scala.concurrent.duration
import scala.concurrent.duration.Duration
import com.akuendig.movie.http.SendReceive

class XrelQueryServiceImplSpec extends TestKit(ActorSystem()) with Specification with Core {
  sequential

  implicit val _duration: Duration = duration.DurationInt(5).seconds
  implicit val _timeout: Timeout = duration.DurationInt(5).seconds
  implicit val _ec: ExecutionContext = system.dispatcher

  val jsonResultLatest =
    """/*-secure-
      {"payload":{"total_count":9453,"pagination":{"current_page":1,"per_page":5,"total_pages":1891},"list":[{"id":"095567c88accd","dirname":"The.Genius.Of.Invention.S01E02.HDTV.x264-FTP","link_href":"http:\/\/www.xrel.to\/tv-nfo\/568525\/The-Genius-Of-Invention-S01E02-HDTV-x264-FTP.html","time":1359672418,"group_name":"FTP","size":{"number":449,"unit":"MB"},"video_type":"HDTV","audio_type":"Stereo","ext_info":{"type":"tv","id":"fd342c011a4b1","title":"The Genius Of Invention","link_href":"http:\/\/www.xrel.to\/tv\/107697\/The-Genius-Of-Invention.html"},"tv_season":1,"tv_episode":2,"flags":{"english":true}},{"id":"7e52575e8accc","dirname":"Criminal.Minds.S08E05.Gute.Erde.GERMAN.DUBBED.DL.1080p.WebHD.x264-TVP","link_href":"http:\/\/www.xrel.to\/tv-nfo\/568524\/Criminal-Minds-S08E05-Gute-Erde-GERMAN-DUBBED-DL-1080p-WebHD-x264-TVP.html","time":1359671908,"group_name":"TVP","size":{"number":1768,"unit":"MB"},"video_type":"Web-Rip","audio_type":"AC3-Dubbed","ext_info":{"type":"tv","id":"92a641ed2ed3","title":"Criminal Minds","link_href":"http:\/\/www.xrel.to\/tv\/11987\/Criminal-Minds.html"},"tv_season":8,"tv_episode":5,"flags":{}},{"id":"e036c2fd8accb","dirname":"Elementary.S01E05.Todesengel.GERMAN.DUBBED.HDTVRiP.XviD-SOF","link_href":"http:\/\/www.xrel.to\/tv-nfo\/568523\/Elementary-S01E05-Todesengel-GERMAN-DUBBED-HDTVRiP-XviD-SOF.html","time":1359671829,"group_name":"SOF","size":{"number":350,"unit":"MB"},"video_type":"HDTV","audio_type":"Stereo","ext_info":{"type":"tv","id":"f61ad2aa17a27","title":"Elementary","link_href":"http:\/\/www.xrel.to\/tv\/96807\/Elementary.html"},"tv_season":1,"tv_episode":5,"flags":{}},{"id":"9731f26b8acca","dirname":"Elementary.S01E05.Todesengel.GERMAN.DUBBED.DL.1080p.WebHD.x264-TVP","link_href":"http:\/\/www.xrel.to\/tv-nfo\/568522\/Elementary-S01E05-Todesengel-GERMAN-DUBBED-DL-1080p-WebHD-x264-TVP.html","time":1359671774,"group_name":"TVP","size":{"number":1759,"unit":"MB"},"video_type":"Web-Rip","audio_type":"AC3-Dubbed","ext_info":{"type":"tv","id":"f61ad2aa17a27","title":"Elementary","link_href":"http:\/\/www.xrel.to\/tv\/96807\/Elementary.html"},"tv_season":1,"tv_episode":5,"flags":{}},{"id":"0e38a3d18acc9","dirname":"Elementary.S01E05.Todesengel.GERMAN.DUBBED.DL.720p.WebHD.h264-euHD","link_href":"http:\/\/www.xrel.to\/tv-nfo\/568521\/Elementary-S01E05-Todesengel-GERMAN-DUBBED-DL-720p-WebHD-h264-euHD.html","time":1359671711,"group_name":"euHD","size":{"number":1400,"unit":"MB"},"video_type":"Web-Rip","audio_type":"AC3-Dubbed","ext_info":{"type":"tv","id":"f61ad2aa17a27","title":"Elementary","link_href":"http:\/\/www.xrel.to\/tv\/96807\/Elementary.html"},"tv_season":1,"tv_episode":5,"flags":{}}]}}
      */"""

  "QueryService" should {
    "query correct address" in {
      var request: HttpRequest = null

      val service = new XrelQueryServiceImpl() with SendReceive {
        def sendReceive(req: HttpRequest)(implicit timeout: Timeout = _timeout) = {
          request = req
          Future(HttpResponse(status = StatusCodes.OK))
        }
      }

      service.fetchSceneRelease(1, 2013, 12)
      request.uri.toString().mustEqual("http://api.xrel.to/api/release/latest.json?page=1&per_page=100&archive=2013-12").orThrow

      service.fetchSceneRelease(1, 203, 1)
      request.uri.toString().mustEqual("http://api.xrel.to/api/release/latest.json?page=1&per_page=100&archive=0203-01").orThrow
    }
//
//    "parse a SceneRelease" in {
//
//      val service = new XrelQueryServiceImpl() with SendReceive {
//        def sendReceive(req: HttpRequest)(implicit timeout: Timeout = _timeout) = {
//          Future(HttpResponse(status = StatusCodes.OK, entity = HttpEntity(jsonResultLatest)))
//        }
//      }
//
//      val rel = service.fetchSceneRelease(1, 2013, 12)
//
//      Await.result(rel, _duration).list must haveSize(100)
//    }

    "parse response correctly" in {
      val service = new XrelQueryServiceImpl() with SendReceive {
        def sendReceive(req: HttpRequest)(implicit timeout: Timeout = _timeout) = {
          Future(HttpResponse(status = StatusCodes.OK, entity = HttpEntity(`application/json`, jsonResultLatest)))
        }
      }

      val response = Await.result(service.fetchSceneRelease(1, 2013, 12), _duration)
      response.list.size.mustEqual(5)
    }

    "serialize the response mesage" in {
      import MovieQueryActor._

      val instance = QuerySceneReleasesResponse(QuerySceneReleases(2013, 12, 1), Some(PagedReleases(1, 10, 100, Set.empty[Release])))
      val out = new ObjectOutputStream(new ByteArrayOutputStream())

      out.writeObject(instance.result)
      out.writeObject(instance.query)
      out.writeObject(instance)
      true.mustEqual(true)
    }
  }
}

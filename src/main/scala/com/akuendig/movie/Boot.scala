package com.akuendig.movie


import akka.pattern.ask
import akka.util.Timeout
import com.akuendig.movie.core.{CoreActors, BootedCore}
import com.akuendig.movie.api.{Web, ApiRoutes}
import com.akuendig.movie.domain.Release
import com.akuendig.movie.storage.ReadModel.{StoreReleasesComplete, StoreReleases}
import java.io.File
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

object Boot extends App with BootedCore with CoreActors with ApiRoutes with Web {

  private implicit val _ec: ExecutionContext = system.dispatcher

  def readOldData() {
    import org.json4s._
    import org.json4s.jackson.JsonMethods.parse

    implicit val _form = DefaultFormats
    implicit val _timeout = Timeout(5.seconds)

    val base = "data"

    for {
      group <- (0 until 6910).grouped(100)

      releases = for {
        i <- group

        file = new File(s"$base/$i.json")
        json = parse(FileInput(file))
        release <- json.extract[Seq[Release]]
      } yield release
    } {
      Await.result(readModelRef ? StoreReleases(releases), _timeout.duration).asInstanceOf[StoreReleasesComplete]
      println(s"Extracted files ${group.min}.json - ${group.max}.json")
    }
  }

  //  readOldData()

  //  system.scheduler.schedule(1.second, 15.seconds, directoryRef, MovieDirectoryActor.MovieDirectoryPing)
}

package com.akuendig.movie.search

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http
import spray.http.{HttpResponse, HttpRequest}


trait SendReceive {
  def sendReceive(req: HttpRequest)(implicit timeout: Timeout): Future[HttpResponse]
}

trait SpraySendReceive extends SendReceive {
  implicit def system: ActorSystem

  private implicit val _ec: ExecutionContext = system.dispatcher

  def sendReceive(req: HttpRequest)(implicit timeout: Timeout = 5.seconds): Future[HttpResponse] =
    IO(Http).ask(req).mapTo[HttpResponse]
}

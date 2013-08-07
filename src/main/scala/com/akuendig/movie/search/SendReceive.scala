package com.akuendig.movie.search

import spray.http.{HttpResponse, HttpRequest}
import scala.concurrent.{ExecutionContext, Future}
import akka.actor.ActorSystem


trait SendReceive {
  def sendReceive: HttpRequest => Future[HttpResponse]
}

trait SpraySendReceive extends SendReceive {
  implicit def system: ActorSystem

  private implicit val _ec: ExecutionContext = system.dispatcher

  lazy val sendReceive: HttpRequest => Future[HttpResponse] =
    spray.client.pipelining.sendReceive
}

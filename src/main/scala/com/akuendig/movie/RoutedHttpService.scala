package com.akuendig.movie

import akka.actor.Actor
import akka.event.Logging
import spray.routing.directives.LogEntry
import spray.routing._
import spray.http.HttpRequest


class RoutedHttpService(route: Route) extends Actor with HttpService {

  def actorRefFactory = context

  def showPath(req: HttpRequest) = LogEntry(s"Method = ${req.method}, Path = ${req.uri}", Logging.InfoLevel)

  def receive: Receive = runRoute(logRequest(showPath _) {
    route
  })
}

package com.akuendig.movie.search

import akka.actor.Actor
import spray.routing.HttpService
import com.akuendig.movie.{InlineHtml, StaticResources, DynamicResources}
import spray.http.HttpRequest
import spray.routing.directives.LogEntry
import akka.event.Logging

// this trait defines our service behavior independently from the service actor
trait MovieSearchService extends HttpService with DynamicResources with StaticResources with InlineHtml {

   def showPath(req: HttpRequest) = LogEntry("Method = %s, Path = %s" format(req.method, req.uri), Logging.InfoLevel)

   val myRoute =
     logRequest(showPath _) {
       staticResources ~ inlineHtml ~ dynamicResources
     }
 }

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MovieSearchServiceActor extends Actor with MovieSearchService {

   // the HttpService trait defines only one abstract member, which
   // connects the services environment to the enclosing actor or test
   def actorRefFactory = context

   // this actor only runs our route, but you could add
   // other things here, like request stream processing
   // or timeout handling
   def receive = runRoute(myRoute)
 }

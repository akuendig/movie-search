package com.akuendig.movie

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 05.08.13
 * Time: 10:55
 * To change this template use File | Settings | File Templates.
 */
// Trait for serving static resources
// Sends 404 for 'favicon.icon' requests and serves static resources in 'bootstrap' folder.
trait StaticResources extends HttpService {

   val staticResources =
     get {
       path("favicon.ico") {
         complete(StatusCodes.NotFound)
       } ~
         path(Rest) {
           path =>
             getFromResource(s"bootstrap/$path")
         } ~
         path("file") {
           getFromResource("application.conf")
         }
     }
 }

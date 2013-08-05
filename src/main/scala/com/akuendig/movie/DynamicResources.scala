package com.akuendig.movie

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 05.08.13
 * Time: 10:56
 * To change this template use File | Settings | File Templates.
 */
trait DynamicResources extends HttpService {

   val dynamicResources =
     path("order" / IntNumber) { id =>
       get {
         complete {
           "Received GET request for order " + id
         }
       } ~
         put {
           complete {
             "Received PUT request for order " + id
           }
         }
     }
 }

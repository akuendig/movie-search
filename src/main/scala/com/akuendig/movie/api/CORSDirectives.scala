package com.akuendig.movie.api

import spray.routing._
import spray.http.HttpHeaders._
import spray.http.StatusCodes.{Forbidden, OK}
import spray.http.{Uri, HttpMethod}
import akka.actor.ActorSystem
import akka.event.Logging


trait CORSDirectives extends RouteConcatenation {
  this: Directives =>

  def system: ActorSystem

  private val log = Logging(system, getClass)

  private def preflightHeaders(origin: Uri, methods: Seq[HttpMethod], headers: Seq[String]): Directive0 =
    respondWithHeaders(
      `Access-Control-Allow-Origin`(origin),
      `Access-Control-Allow-Credentials`(allow = true),
      `Access-Control-Allow-Methods`(methods),
      `Access-Control-Allow-Headers`(headers)
    )

  private def cqrsHeaders(origin: Uri): Directive0 =
    respondWithHeaders(
      `Access-Control-Allow-Origin`(origin),
      `Access-Control-Allow-Credentials`(allow = true)
    )

  private val methodHeader: Directive1[HttpMethod] = headerValue {
    case `Access-Control-Request-Method`(mtd) => Some(mtd)
    case _ => None
  }

  private val headersHeader: Directive1[Seq[String]] = headerValue {
    case `Access-Control-Request-Headers`(headers) => Some(headers)
    case _ => None
  }

  private val originHeader: Directive1[Uri] = headerValue {
    case Origin(origin) => Some(origin)
    case _ => None
  }

  def cqrsAllow(origins: String*)(methods: HttpMethod*): Directive0 = mapInnerRoute { (route: Route) =>
    val allOrigins = origins.contains("*")
    val allowedOrigins = origins.map(Uri.apply)
    val allowedMethods = methods

    val preflight = options {
      val allHeaders = methodHeader & headersHeader & originHeader

      allHeaders {
        (method, headers, clientOrigin) =>
          if (allowedMethods.contains(method) && allowedOrigins.contains(clientOrigin))
            preflightHeaders(clientOrigin, methods, headers) {
              complete(OK, Nil, "Success")
            }
          else
            complete(Forbidden, Nil, "Invalid origin") // Maybe, a Rejection will fit better
      }
    }

    val wrapped = originHeader {
      clientOrigin =>
        if (allOrigins || allowedOrigins.contains(clientOrigin))
          cqrsHeaders(clientOrigin)(route)
        else
          complete(Forbidden, Nil, "Invalid origin") // Maybe, a Rejection will fit better
    }

    preflight ~ wrapped ~ route
  }
}

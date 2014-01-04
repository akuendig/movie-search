package com.akuendig.movie.api

import spray.routing._
import spray.http.HttpHeaders._
import spray.http.StatusCodes.{Forbidden, OK}
import spray.http._
import akka.actor.ActorSystem
import akka.event.Logging
import spray.http.SomeOrigins
import scala.Some


trait CORSDirectives extends RouteConcatenation {
  this: Directives =>

  def system: ActorSystem

  private val log = Logging(system, getClass)

  private def preflightHeaders(origins: AllowedOrigins, methods: Seq[HttpMethod], headers: Seq[String]): Directive0 =
    respondWithHeaders(
      `Access-Control-Allow-Origin`(origins),
      `Access-Control-Allow-Credentials`(allow = true),
      `Access-Control-Allow-Methods`(methods),
      `Access-Control-Allow-Headers`(headers)
    )

  private def cqrsHeaders(origins: AllowedOrigins): Directive0 =
    respondWithHeaders(
      `Access-Control-Allow-Origin`(origins),
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

  private val originHeader: Directive1[Seq[HttpOrigin]] = headerValue {
    case Origin(origin) => Some(origin)
    case _ => None
  }

  def cqrsAllow(origins: String*)(allowedMethods: HttpMethod*): Directive0 = mapInnerRoute {
    (route: Route) =>
      val allOrigins = origins.contains("*")
      // Convert to the Spray AllowedOrigins class
      val httpOrigins = origins.map(HttpOrigin.apply)
      val allowedOrigins =
        if (allOrigins) AllOrigins
        else SomeOrigins(httpOrigins)

      // This is a rout that responds to OPTION requests and checks al CQRS headers.
      // The preflight is used by the browser to check, if it is safe to make a CQRS
      // call to this resource.
      val preflight = options {
        val allHeaders = methodHeader & headersHeader & originHeader

        allHeaders {
          (method, headers, clientOrigin) =>
            val originMatches = allOrigins || httpOrigins.contains(clientOrigin)
            val methodMatches = allowedMethods.contains(method)

            if (originMatches && methodMatches)
              preflightHeaders(allowedOrigins, allowedMethods, headers) {
                complete(OK, Nil, "Success")
              }
            else
              complete(Forbidden, Nil, "Invalid origin") // Maybe, a Rejection will fit better
        }
      }

      // The wrapper then catches all other request containing an Origin header and
      // checks that one against the given ones. If we again find a match, we let the
      // request through. TODO: We should also check the method.
      val wrapped = originHeader {
        clientOrigin =>
          val originMatches = allOrigins || httpOrigins.contains(clientOrigin)

          if (originMatches)
            cqrsHeaders(SomeOrigins(clientOrigin))(route)
          else
            complete(Forbidden, Nil, "Invalid origin") // Maybe, a Rejection will fit better
      }

      preflight ~ wrapped ~ route
  }
}

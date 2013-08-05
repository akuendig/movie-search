package com.akuendig.movie

import spray.routing.HttpService
import spray.http.MediaTypes._

// Trait for serving a page with inline html
trait InlineHtml extends HttpService {

  val inlineHtml =
    get {
      path("") {
        respondWithMediaType(`text/html`) {
          // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to
                  <i>spray-routing</i>
                  on
                  <i>spray-can</i>
                  !</h1>
                <p>
                  <h2>Some Twirl and static pages:</h2>
                  <a href="/index">Simple index page using Twirl</a> <br/>
                  <a href="/index2">Another Twirl page using a Twitter Bootstrap template with dynamic data and using static css and js resources</a> <br/>
                  <a href="/file">Getting content from a static file</a> <br/>
                </p>
              </body>
            </html>
          }
        }
      }
    }
}

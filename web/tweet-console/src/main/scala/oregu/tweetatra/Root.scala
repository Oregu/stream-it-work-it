package oregu.tweetatra

import com.twitter.finagle.httpx.Request
import com.twitter.finatra.http.Controller

class Root extends Controller {
  get("/") { request: Request =>
    "hi"
  }
}

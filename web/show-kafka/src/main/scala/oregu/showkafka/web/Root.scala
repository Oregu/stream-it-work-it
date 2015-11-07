package oregu.showkafka.web

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class Root extends Controller {
  get("/") { request: Request =>
    "<div>Go speak to <a href='/camus'>Camus</a>.</div>"
  }
  get("/:*") { request: Request =>
   response.ok.file(request.params("*"))
  }
}

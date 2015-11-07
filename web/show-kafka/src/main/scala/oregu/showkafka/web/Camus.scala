package oregu.showkafka.web

import scala.collection.concurrent.TrieMap

import javax.inject.Inject

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.response.Mustache

import oregu.showkafka.service.CamusService


object State {
  val stats = new TrieMap[Int, Long]
}

@Mustache("camus")
case class CamusView()

class Camus @Inject()(
  camusService: CamusService
) extends Controller {

  get("/camus") { request: Request =>
    camusService.service(State.stats)
    request.contentType match {
      case Some("application/json") =>
        response.ok.json(State.stats.toSeq.sortBy(_._1))
      case _ =>
        CamusView()
    }
  }
}

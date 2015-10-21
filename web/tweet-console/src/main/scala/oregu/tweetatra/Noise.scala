package oregu.tweetatra

import scala.collection.JavaConversions._

import com.twitter.finagle.httpx.Request
import com.twitter.finatra.http.Controller

import org.apache.kafka.clients.consumer._

class Noise extends Controller {
  get("/noise") { request: Request =>
    val props = Map[String, Object](
      "groupid" -> "group",
      "auto.commit" -> "true",
      "zk.connect" -> "localhost:2181")

    val connector = new KafkaConsumer[String, String](props)
    val topic = "noise-extract"

    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run() {
        connector.close()
      }
    })

    connector.subscribe(topic)
    val recs = connector.poll(1000)

    var builder = new StringBuilder
    for(rec <- recs) {
      builder ++= rec._1
      for (rec2 <- rec._2.records(rec)) {
        builder ++= rec2._1 + rec2._2
      }
    }

    connector.close()
    builder.toString()
  }
}

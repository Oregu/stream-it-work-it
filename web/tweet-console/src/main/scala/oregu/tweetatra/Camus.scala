package oregu.tweetatra

import java.util.Properties

import kafka.consumer._
import kafka.serializer.DefaultDecoder

import scala.collection.JavaConversions._
import scala.collection.concurrent.TrieMap

import com.twitter.finagle.httpx.Request
import com.twitter.finatra.http.Controller


object Stats {
  val stats = new TrieMap[Int, Long]
}

class Camus extends Controller {
  get("/noise") { request: Request =>
    val props = Map[String, Object](
      "group.id" -> "default",
      "zookeeper.connect" -> "localhost:2181",
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "partition.assignment.strategy" -> "roundrobin",
      "consumer.timeout.ms" -> "100")

    val propsProps = new Properties
    propsProps.putAll(props)

    val topic = "camus-stat"
    val connector = Consumer.create(new ConsumerConfig(propsProps))

    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run() {
        connector.shutdown()
      }
    })

    val stream: KafkaStream[Array[Byte], Array[Byte]] =
      connector.createMessageStreamsByFilter(new Whitelist(topic), 1, new DefaultDecoder(), new DefaultDecoder()).head

    scala.util.control.Exception.ignoring(classOf[Exception]) {
      while (stream.iterator().hasNext()) {
        val msg = stream.iterator().next()
        val k = msg.key().toString.toInt
        val v = msg.message().toString.toLong
        if (Stats.stats.putIfAbsent(k, v).isDefined) {
          Stats.stats.put(k, Stats.stats(k) + v)
        }
      }
    }

    var builder = new StringBuilder()

    for (data <- Stats.stats) {
      builder.append("length: ").append(data._1)
      builder.append("\tcount: ").append(data._2).append("\n")
    }

    connector.shutdown()
    if (builder.isEmpty) "None written" else builder.toString()
  }
}

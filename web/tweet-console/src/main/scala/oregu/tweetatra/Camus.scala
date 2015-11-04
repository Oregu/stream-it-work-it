package oregu.tweetatra

import java.util.Properties

import com.twitter.finagle.httpx.Request
import com.twitter.finatra.http.Controller
import kafka.consumer._

import scala.collection.JavaConversions._
import scala.collection.concurrent.TrieMap
import scala.collection.immutable.ListMap
import scala.util.control.Exception._


object Stats {
  val stats = new TrieMap[Int, Long]
}

class Camus extends Controller {
  get("/camus") { request: Request =>
    val props = Map[String, Object](
      "group.id" -> "default",
      "zookeeper.connect" -> "kafka:2181",
      "bootstrap.servers" -> "kafka:9092",
      "key.deserializer" -> "org.apache.kafka.common.serialization.ByteArrayDeserializer",
      "value.deserializer" -> "org.apache.kafka.common.serialization.ByteArrayDeserializer",
      "partition.assignment.strategy" -> "roundrobin",
      "consumer.timeout.ms" -> "100")

    val propsProps = new Properties
    propsProps.putAll(props)

    val topic = "camus-spoke"
    val connector = Consumer.create(new ConsumerConfig(propsProps))

    try {
      val stream: KafkaStream[Array[Byte], Array[Byte]] =
        connector.createMessageStreamsByFilter(new Whitelist(topic)).head
      while (stream.iterator().hasNext()) {
        val msg = stream.iterator().next()
        logger.debug("Got message: (" + msg.key() + ", " + msg.message() + ")")

        val k = (Option(msg.key()) map (java.nio.ByteBuffer.wrap(_).getInt)) getOrElse 0
        val v = (Option(msg.message()) map (java.nio.ByteBuffer.wrap(_).getLong)) getOrElse 0L

        // TODO: Non-atomic
        if (Stats.stats.putIfAbsent(k, v).isDefined) {
          Stats.stats.put(k, Stats.stats(k) + v)
        }
      }
    }
    catch {
      case e: Exception =>
        logger.error("Can't read Kafka stream.", e)
    }
    finally {
      connector.shutdown()
    }

    var builder = new StringBuilder()

    for (data <- Stats.stats.toSeq.sortBy(_._1)) {
      builder.append("length: ").append(data._1)
      builder.append("\tcount: ").append(data._2).append("\n")
    }

    if (builder.isEmpty) "None written" else builder.toString()
  }
}

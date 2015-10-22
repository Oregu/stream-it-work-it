package oregu.tweetatra

import java.util.Properties

import kafka.serializer.DefaultDecoder

import scala.collection.JavaConversions._

import com.twitter.finagle.httpx.Request
import com.twitter.finatra.http.Controller

import kafka.consumer._

class Noise extends Controller {
  get("/noise") { request: Request =>
    val props = Map[String, Object](
      "group.id" -> "default",
      "zookeeper.connect" -> "localhost:2181",
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "partition.assignment.strategy" -> "roundrobin")

    val propsProps = new Properties
    propsProps.putAll(props)

    val topic = "noise-extract"
    val connector = Consumer.create(new ConsumerConfig(propsProps))

    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run() {
        connector.shutdown()
      }
    })

    val stream: KafkaStream[Array[Byte], Array[Byte]] =
      connector.createMessageStreamsByFilter(new Whitelist(topic), 1, new DefaultDecoder(), new DefaultDecoder()).head

    val msgAndMeta = stream.iterator().next()

    var builder = new StringBuilder(msgAndMeta.topic)
    builder ++= "\nkey:"
    builder ++= Option(msgAndMeta.key()).toString
    builder ++= "\nmsg:"
    builder ++= new String(msgAndMeta.message(), "UTF-8")
    builder ++= "\npartition:"
    builder ++= Option(msgAndMeta.partition).toString
    builder ++= "\noffset:"
    builder ++= Option(msgAndMeta.offset).toString

    connector.shutdown()
    builder.toString()
  }
}

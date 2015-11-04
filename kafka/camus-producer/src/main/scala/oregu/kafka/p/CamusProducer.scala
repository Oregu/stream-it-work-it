package oregu.kafka.p

import java.nio.charset.CodingErrorAction

import org.apache.kafka.clients.producer.ProducerConfig._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.collection.JavaConversions._
import scala.io.Codec
import scala.io.Source

object CamusProducer {
  def main(args: Array[String]): Unit = {

    val configs = Map[String, Object](
      BOOTSTRAP_SERVERS_CONFIG      -> "kafka:9092",
      KEY_SERIALIZER_CLASS_CONFIG   -> "org.apache.kafka.common.serialization.StringSerializer",
      VALUE_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[Nothing, String](configs)

    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run() {
        producer.close()
      }
    })

    implicit val codec = Codec.UTF8
    codec.onMalformedInput(CodingErrorAction.IGNORE)

    val source = Source.fromInputStream(getClass.getResourceAsStream("/camus.txt"))
    val lines = try source.mkString finally source.close()

    val kafkaTopic = "camus-speaks-words"
    for (word <- lines.split("\\s+")) {
      producer.send(new ProducerRecord(kafkaTopic, word))
    }

    producer.close()
  }
}

package oregu.kafka.p

import scala.collection.JavaConversions._

import scala.util.Random
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.clients.producer.ProducerConfig._

object NoiseProducer {
  def main(args: Array[String]): Unit = {
    val topicName = if (args.length == 0) "noisenoise" else args(0)

    val configs = Map[String, Object](
        BOOTSTRAP_SERVERS_CONFIG      -> "127.0.0.1:9092",
        KEY_SERIALIZER_CLASS_CONFIG   -> "org.apache.kafka.common.serialization.StringSerializer",
        VALUE_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringSerializer")

    val strProducer = new KafkaProducer[Nothing, String](configs)

    val text = List("some", "random", "text", "emulate", "twitter", "gibberish")
    while (true) {
      val rec = new ProducerRecord(topicName, Random.shuffle(text).mkString(" "))
      strProducer.send(rec)
    }
  }
}

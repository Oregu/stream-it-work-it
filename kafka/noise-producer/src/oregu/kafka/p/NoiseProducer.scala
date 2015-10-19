package oregu.kafka.p

import scala.collection.JavaConversions._

import scala.util.Random
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.clients.producer.ProducerConfig._

object NoiseProducer {
  def main(args: Array[String]): Unit = {

    val configs = Map[String, Object](
      BOOTSTRAP_SERVERS_CONFIG      -> "kafka:9092",
      KEY_SERIALIZER_CLASS_CONFIG   -> "org.apache.kafka.common.serialization.StringSerializer",
      VALUE_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringSerializer")

    val kafkaTopic = "noisenoise"

    val producer = new KafkaProducer[Nothing, String](configs)

    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run() {
        producer.close()
      }
    })

    val text = List("some", "funky", "text", "emulate", "twitter", "shmitter")
    while (true) {
      val rec = new ProducerRecord(kafkaTopic, Random.shuffle(text).mkString(" "))
      producer.send(rec)
      Thread.sleep(Random.nextInt(500))
    }
  }
}

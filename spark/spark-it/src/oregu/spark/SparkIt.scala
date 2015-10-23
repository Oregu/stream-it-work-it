package oregu.spark

import java.util.Properties

import kafka.serializer.StringDecoder
import org.apache.kafka.clients.producer._
import org.apache.kafka.clients.producer.ProducerConfig.{BOOTSTRAP_SERVERS_CONFIG, KEY_SERIALIZER_CLASS_CONFIG, VALUE_SERIALIZER_CLASS_CONFIG}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkIt {

  def main(args: Array[String]): Unit = {

    val kafkaConfigs = Map[String, String](
      BOOTSTRAP_SERVERS_CONFIG      -> "kafka:9092",
      KEY_SERIALIZER_CLASS_CONFIG   -> "org.apache.kafka.common.serialization.StringSerializer",
      VALUE_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringSerializer")

    val kafkaTopics = Set("noisenoise")

    val sparkConf = new SparkConf().setAppName("SparkItKafkaWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    val directKafkaStream = KafkaUtils.createDirectStream[
        String, String, StringDecoder, StringDecoder](
        ssc, kafkaConfigs, kafkaTopics)

    val lines = directKafkaStream.map(_._2)
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)

    sendToKafka(wordCounts)

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }

  def sendToKafka(wordCounts: DStream[(String, Long)]) = {
    val configs = new Properties
    configs.put(BOOTSTRAP_SERVERS_CONFIG,      "kafka:9092")
    configs.put(KEY_SERIALIZER_CLASS_CONFIG,   "org.apache.kafka.common.serialization.StringSerializer")
    configs.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[Nothing, String](configs)

    val topic = "noise-extract"
    wordCounts.foreachRDD { rdd =>
      rdd.foreach { count =>
        val rec = new ProducerRecord(topic, count._2.toString)
        producer.send(rec)
      }
    }
    producer.close()
  }
}

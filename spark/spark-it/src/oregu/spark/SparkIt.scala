package oregu.spark

import java.util.Properties

import kafka.serializer.StringDecoder
import org.apache.kafka.clients.producer._
import org.apache.kafka.clients.producer.ProducerConfig._
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{InputDStream, DStream}
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkIt {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("SparkItKafkaWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(2))

    val wordCounts = spark(fromKafka(ssc))
    toKafka(wordCounts)

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }

  def spark(directKafkaStream: InputDStream[(String, String)]): DStream[(String, Long)] = {
    val lines = directKafkaStream.map(_._2)
    val words = lines.flatMap(_.split(" "))
    words.map(x => (x, 1L)).reduceByKey(_ + _)
  }

  def fromKafka(ssc: StreamingContext): (InputDStream[(String, String)]) = {
    val kafkaConfigs = Map[String, String](
      BOOTSTRAP_SERVERS_CONFIG      -> "kafka:9092",
      KEY_SERIALIZER_CLASS_CONFIG   -> "org.apache.kafka.common.serialization.StringSerializer",
      VALUE_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringSerializer")
    val kafkaTopics = Set("camus")

    KafkaUtils.createDirectStream[
        String, String, StringDecoder, StringDecoder](
        ssc, kafkaConfigs, kafkaTopics)
  }

  def toKafka(wordCounts: DStream[(String, Long)]) = {
    val configs = new Properties
    configs.put(BOOTSTRAP_SERVERS_CONFIG,      "kafka:9092")
    configs.put(KEY_SERIALIZER_CLASS_CONFIG,   "org.apache.kafka.common.serialization.StringSerializer")
    configs.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")

    val topic = "camus-stat"
    wordCounts.foreachRDD { rdd =>
      rdd.foreach { count =>
        val producer = new KafkaProducer[Nothing, String](configs)
        val rec = new ProducerRecord(topic, count._2.toString)
        producer.send(rec)
        producer.close()
      }
    }
  }
}

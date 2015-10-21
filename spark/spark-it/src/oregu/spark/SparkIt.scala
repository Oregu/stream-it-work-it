package oregu.spark

import kafka.serializer.StringDecoder
import org.apache.kafka.clients.producer.ProducerConfig._
import org.apache.spark.SparkConf
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
    wordCounts.print()

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }
}

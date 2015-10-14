package oregu.kafka.p

object NoiseProducer {
  def main(args: Array[String]): Unit = {
    val topicName =
      if(args.length == 0) "testTopic"
        else args(0)

    val strProducer = Producer[String](topicName)

    for (ln <- io.Source.stdin.getLines) strProducer.send(ln)
  }
}

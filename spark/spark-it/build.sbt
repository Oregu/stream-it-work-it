name := "spark-it"
version := "0.2"
scalaVersion := "2.10.6"
scalaSource in Compile := baseDirectory.value / "src"

mainClass in assembly := Some("oregu.spark.SparkIt")

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.10" % "1.5.1" % "provided",
  "org.apache.spark" % "spark-streaming_2.10" % "1.5.1" % "provided",
  "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.5.1"
)

assemblyMergeStrategy in assembly := {
  case PathList("org", "apache", "spark", "unused", "UnusedStubClass.class") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

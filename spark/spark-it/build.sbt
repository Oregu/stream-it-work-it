scalaSource in Compile := baseDirectory.value / "src"

lazy val spark_it = (project in file(".")).settings(
  name := "spark-it",
  version := "0.2",
  libraryDependencies ++= Seq(
    "org.apache.spark" % "spark-core_2.10" % "1.5.1" % "provided",
    "org.apache.spark" % "spark-streaming_2.10" % "1.5.1" % "provided",
    "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.5.1"
))

assemblyMergeStrategy in assembly := {
  case PathList("org", "apache", "spark", "unused", "UnusedStubClass.class") => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

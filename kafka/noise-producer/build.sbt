name := "noise-producer"
version := "0.2"
scalaVersion := "2.11.7"
scalaSource in Compile := baseDirectory.value / "src"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "0.8.2.2"
)

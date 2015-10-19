scalaSource in Compile := baseDirectory.value / "src"

lazy val noise_producer = (project in file(".")).settings(
  name := "noise-producer",
  version := "0.2",
  libraryDependencies ++= Seq(
    "org.apache.kafka" % "kafka-clients" % "0.8.2.1"
  )
)

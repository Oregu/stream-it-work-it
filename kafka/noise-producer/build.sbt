val kafka = "org.apache.kafka" % "kafka-clients" % "0.8.2.1"

scalaSource in Compile := baseDirectory.value / "src"

lazy val root = (project in file(".")).settings(
  name := "noise-producer",
  version := "0.2",
  libraryDependencies += kafka
)

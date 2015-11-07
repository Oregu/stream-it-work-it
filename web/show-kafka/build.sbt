name := "show-kafka"
version := "0.2"
scalaVersion := "2.11.7"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Twitter Maven" at "https://maven.twttr.com"
)

Revolver.settings

libraryDependencies ++= Seq(
  "com.twitter.finatra" % "finatra-http_2.11" % "2.1.1",
  "com.twitter.finatra" % "finatra-slf4j_2.11" % "2.1.1",
  ("org.apache.kafka" % "kafka_2.11" % "0.8.2.2").exclude("org.slf4j", "slf4j-log4j12"),
  "ch.qos.logback" % "logback-classic" % "1.1.3"
)

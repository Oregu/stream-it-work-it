name := "tweet-console"
version := "0.2"
scalaVersion := "2.11.7"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Twitter Maven" at "https://maven.twttr.com"
)

libraryDependencies ++= Seq(
  "com.twitter.finatra" % "finatra-http_2.11" % "2.1.0",
  "com.twitter.finatra" % "finatra-slf4j_2.11" % "2.1.0",
  "org.apache.kafka" % "kafka-clients" % "0.8.2.1",
  "ch.qos.logback" % "logback-classic" % "1.1.3"
)

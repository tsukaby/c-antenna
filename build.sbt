scalaVersion := "2.11.2"

name := "c-antenna"

version := "1.0"

libraryDependencies ++= Seq(
  "informa" % "informa" % "0.6.0",
  "redis.clients" % "jedis" % "2.6.0",
  "biz.source_code" % "base64coder" % "2010-12-19"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

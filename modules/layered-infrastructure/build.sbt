import sbt.Keys._

resolvers += "Maven Central Server" at "http://repo1.maven.org/maven2"

resolvers += "ATILIKA dependencies" at "http://www.atilika.org/nexus/content/repositories/atilika"

resolvers += "Sedis Repo" at "http://pk11-scratch.googlecode.com/svn/trunk"

resolvers += "Akka-Quartz Repo" at "http://repo.theatr.us"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.33",
  "org.scalikejdbc" %% "scalikejdbc" % "2.2.+",
  "org.scalikejdbc" %% "scalikejdbc-config" % "2.2.+",
  "org.scalikejdbc" %% "scalikejdbc-play-plugin" % "2.3.4",
  "org.scalikejdbc" %% "scalikejdbc-play-fixture-plugin" % "2.3.4",
  "org.scalikejdbc" %% "scalikejdbc-test" % "2.2.+" % "test",
  "com.h2database" % "h2" % "1.4.181" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.rometools" % "rome" % "1.5.0", //RSS
  "redis.clients" % "jedis" % "2.6.0", //Redis
  "biz.source_code" % "base64coder" % "2010-12-19", //Redisへオブジェクト格納用
  "com.github.detro" % "phantomjsdriver" % "1.2.0" exclude("org.seleniumhq.selenium", "jetty-repacked"), // 画面キャプチャ用
  "org.atilika.kuromoji" % "kuromoji" % "0.7.7", // 形態素解析用
  "com.typesafe.akka" %% "akka-actor" % "2.3.6", // batch用
  "io.spray" %% "spray-client" % "1.3.1", // 軽量HTTPクライアント 他のライブラリを使うまでもない部分で使う
  "org.scalaz" %% "scalaz-core" % "7.0.6", // より良い構文のため
  "us.theatr" %% "akka-quartz" % "0.3.0", // cron形式でジョブ登録・実行するためのもの
  "com.github.nscala-time" %% "nscala-time" % "1.4.0", // 日付用
  "org.apache.xmlrpc" % "xmlrpc-common" % "3.1.3", //XML RPC
  "org.apache.xmlrpc" % "xmlrpc-client" % "3.1.3", //XML RPC
  "xml-apis" % "xml-apis" % "2.0.2", //XML RPC
  "com.sksamuel.scrimage" %% "scrimage-core" % "1.4.2",
  "org.specs2" %% "specs2-core" % "2.3.13" % "test",
  "org.specs2" %% "specs2-mock" % "2.3.13" % "test"
)

lazy val layeredInfrastructure = (project in file("."))
  .settings(
    name := "layered-infrastructure",
    version := "1.0",
    organization := "com.tsukaby",
    scalaVersion in ThisBuild := "2.11.4",
    scalacOptions += "-feature",
    javaOptions in Test += "-Dconfig.file=test.conf"
  )

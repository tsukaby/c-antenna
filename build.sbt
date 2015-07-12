import play.PlayImport.PlayKeys._
import sbt.Keys._

scalikejdbcSettings

lazy val json4sVersion = "3.2.11"

lazy val scalikejdbcVersion = "2.2.7"

lazy val scalikejdbcPlayVersion = "2.3.6"

lazy val akkaVersion = "2.3.11"

lazy val scrimageVersion = "2.0.2"

lazy val commonSettings = Seq(
  version := "0.0.4",
  organization := "com.tsukaby",
  scalaVersion in ThisBuild := "2.11.7",
  scalacOptions += "-feature",
  javaOptions in Test += "-Dconfig.file=conf/test.conf",
  test in assembly := {},
  doc in Compile <<= target.map(_ / "none"),
  assemblyMergeStrategy in assembly := {
    case n if n.startsWith("reference.conf") => MergeStrategy.concat
    case "META-INF/MANIFEST.MF" => MergeStrategy.discard
    case x => MergeStrategy.first
  },
  resolvers ++= Seq(
    "Maven Central Server" at "http://repo1.maven.org/maven2",
    "ATILIKA dependencies" at "http://www.atilika.org/nexus/content/repositories/atilika",
    "Sedis Repo" at "http://pk11-scratch.googlecode.com/svn/trunk",
    "Akka-Quartz Repo" at "http://repo.theatr.us",
    "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
  ),
  libraryDependencies ++= Seq(
    "io.kamon" %% "kamon-core" % "0.4.0",
    "io.kamon" %% "kamon-scala" % "0.4.0",
    "io.kamon" %% "kamon-akka" % "0.4.0",
    "io.kamon" %% "kamon-statsd" % "0.4.0",
    "io.kamon" %% "kamon-datadog" % "0.4.0",
    // "io.kamon" %% "kamon-play" % "0.4.0", It depend play_ws 2.3.8.
    "io.kamon" % "sigar-loader" % "1.6.5-rev001",
    specs2 % Test
  ),
  checksums in update := Nil
)

lazy val infrastructure = (project in file("modules/infrastructure"))
  .settings(
    name := "infrastructure",
    libraryDependencies ++= Seq(
      "mysql" % "mysql-connector-java" % "5.1.36",
      "org.scalikejdbc" %% "scalikejdbc" % scalikejdbcVersion,
      "org.scalikejdbc" %% "scalikejdbc-config" % scalikejdbcVersion,
      "org.scalikejdbc" %% "scalikejdbc-test" % scalikejdbcVersion % "test",
      "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.4.0",
      "org.scalikejdbc" %% "scalikejdbc-play-fixture" % "2.4.0",
      "org.scalikejdbc" %% "scalikejdbc-play-dbapi-adapter" % "2.4.0",
      "com.h2database" % "h2" % "1.4.187" % "test",
      "ch.qos.logback" % "logback-classic" % "1.1.3",
      "com.rometools" % "rome" % "1.5.1", //RSS
      "redis.clients" % "jedis" % "2.6.2", //Redis
      "biz.source_code" % "base64coder" % "2010-12-19", //Redisへオブジェクト格納用
      "com.github.detro" % "phantomjsdriver" % "1.2.0" exclude("org.seleniumhq.selenium", "jetty-repacked"), // 画面キャプチャ用
      "org.atilika.kuromoji" % "kuromoji" % "0.7.7", // 形態素解析用
      "com.typesafe.akka" %% "akka-actor" % akkaVersion, // batch用
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "io.spray" %% "spray-client" % "1.3.3", // 軽量HTTPクライアント 他のライブラリを使うまでもない部分で使う
      "us.theatr" %% "akka-quartz" % "0.3.0", // cron形式でジョブ登録・実行するためのもの
      "com.github.nscala-time" %% "nscala-time" % "2.0.0", // 日付用
      "org.apache.xmlrpc" % "xmlrpc-common" % "3.1.3", //XML RPC
      "org.apache.xmlrpc" % "xmlrpc-client" % "3.1.3", //XML RPC
      "xml-apis" % "xml-apis" % "2.0.2", //XML RPC
      "com.sksamuel.scrimage" %% "scrimage-core" % scrimageVersion,
      "com.sksamuel.scrimage" %% "scrimage-io" % scrimageVersion,
      "com.sksamuel.scrimage" %% "scrimage-filters" % scrimageVersion,
      "org.apache.xmlgraphics" % "batik-transcoder" % "1.8" // ClassNotFoundException org.apache.batik.transcoder.TranscoderException
    )
  )
  .settings(commonSettings: _*)

lazy val domain = (project in file("modules/domain"))
  .dependsOn(infrastructure % "test->test;test->compile;compile->compile")
  .settings(
    name := "domain"
  )
  .settings(commonSettings: _*)

lazy val web = (project in file("modules/web"))
  .enablePlugins(PlayScala)
  .dependsOn(
    domain % "test->test;test->compile;compile->compile",
    infrastructure % "test->test;compile->compile")
  .settings(commonSettings: _*)
  .settings(
    // QueryPathBinderを使う為に以下をroutesにインポート
    routesImport ++= Seq(
      "com.tsukaby.c_antenna.controller.Implicits._",
      "com.tsukaby.c_antenna.entity._",
      "com.tsukaby.c_antenna.db.entity._"
    ),
    libraryDependencies ++= Seq(
      "org.scalikejdbc" %% "scalikejdbc-config" % scalikejdbcVersion,
      "org.scalikejdbc" %% "scalikejdbc-play-plugin" % scalikejdbcPlayVersion,
      "org.scalikejdbc" %% "scalikejdbc-play-fixture-plugin" % scalikejdbcPlayVersion,
      "org.json4s" %% "json4s-native" % json4sVersion,
      "org.json4s" %% "json4s-ext" % json4sVersion,
      "com.github.tototoshi" %% "play-json4s-native" % "0.4.0",
      "com.github.tototoshi" %% "play-json4s-test-native" % "0.4.0" % "test",
      "org.flywaydb" %% "flyway-play" % "2.0.1"
    ),
    doc in Compile <<= target.map(_ / "none"),    // QueryPathBinderを使う為に以下をroutesにインポート
    //playRunHooks <+= baseDirectory.map(base => Grunt(base)),
    excludeFilter in Assets := "*.ts" || "scss" || "test" || "typings",
    name := "web"
  )

lazy val root = (project in file("."))
  .aggregate(web, domain, infrastructure)
  .dependsOn(web, domain, infrastructure)
  .enablePlugins(PlayScala)
  .settings(commonSettings: _*)
  .settings(
    name := "c-antenna"
  )

lazy val batch = (project in file("modules/batch"))
  .dependsOn(
    domain % "test->test;test->compile;compile->compile",
    infrastructure % "test->test;compile->compile")
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.json4s" %% "json4s-native" % json4sVersion,
      "org.json4s" %% "json4s-ext" % json4sVersion
    ),
    name := "batch"
  )
  .settings(commonSettings: _*)
  .settings(
    name := "c-antenna-batch",
    mainClass in assembly := Some("com.tsukaby.c_antenna.Main"),
    assemblyOutputPath in assembly := file("./c-antenna-batch.jar")
  )

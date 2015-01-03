import play.PlayImport.PlayKeys._
import sbt.Keys._

lazy val json4sVersion = "3.2.10"

lazy val commonSettings = Seq(
  version := "1.0",
  organization := "com.tsukaby",
  scalaVersion in ThisBuild := "2.11.4",
  scalacOptions += "-feature",
  javaOptions in Test += "-Dconfig.file=conf/test.conf",
  test in assembly := {},
  doc in Compile <<= target.map(_ / "none"),
  assemblyMergeStrategy in assembly := {
    case "META-INF/MANIFEST.MF" => MergeStrategy.discard
    case x => MergeStrategy.first
  },
  libraryDependencies ++= Seq(
    "mysql" % "mysql-connector-java" % "5.1.33",
    "org.scalikejdbc" %% "scalikejdbc" % "2.2.+",
    "org.scalikejdbc" %% "scalikejdbc-config" % "2.2.+",
    "org.scalikejdbc" %% "scalikejdbc-play-plugin" % "2.3.4",
    "org.scalikejdbc" %% "scalikejdbc-play-fixture-plugin" % "2.3.4",
    "org.scalikejdbc" %% "scalikejdbc-test" % "2.2.+" % "test",
    "org.json4s" %% "json4s-native" % json4sVersion,
    "org.json4s" %% "json4s-ext" % json4sVersion,
    "com.github.tototoshi" %% "play-json4s-native" % "0.3.0",
    "com.github.tototoshi" %% "play-json4s-test-native" % "0.3.0" % "test"
  )
)

lazy val layeredInfrastructure = (project in file("../layered-infrastructure"))
  .settings(commonSettings: _*)

lazy val layeredDomain = (project in file("../layered-domain"))
  .dependsOn(layeredInfrastructure % "test->test;test->compile;compile->compile")
  .settings(commonSettings: _*)

lazy val layeredApplication = (project in file("."))
  .enablePlugins(PlayScala)
  .dependsOn(
    layeredDomain % "test->test;test->compile;compile->compile",
    layeredInfrastructure % "test->test;compile->compile")
  .settings(commonSettings: _*)
  .settings(
    // QueryPathBinderを使う為に以下をroutesにインポート
    routesImport ++= Seq(
      "com.tsukaby.c_antenna.controller.Implicits._",
      "com.tsukaby.c_antenna.entity._",
      "com.tsukaby.c_antenna.db.entity._"
    ),
    playRunHooks <+= baseDirectory.map(base => Grunt(base)),
    unmanagedResourceDirectories in Assets += baseDirectory.value / "ui",
    excludeFilter in Assets := "*.ts" || "scss" || "*.map" || "test" || "typings",
    name := "layered-application"
  )

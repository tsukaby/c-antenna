import play.PlayImport.PlayKeys._
import sbt.Keys._

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
    "io.spray" %%  "spray-json" % "1.3.1"
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
    routesImport += "com.tsukaby.c_antenna.controller.Implicits._",
    playRunHooks <+= baseDirectory.map(base => Grunt(base)),
    unmanagedResourceDirectories in Assets += baseDirectory.value / "ui",
    excludeFilter in Assets := "*.ts" || "scss" || "*.map" || "test" || "typings",
    name := "layered-application"
  )

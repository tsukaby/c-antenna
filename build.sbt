import play.PlayScala
import play.PlayImport.PlayKeys._
import sbt.Keys._

playRunHooks <+= baseDirectory.map(base => Grunt(base))

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
  }
)

lazy val layeredInfrastructure = (project in file("modules/layered-infrastructure"))
  .settings(commonSettings: _*)

lazy val layeredDomain = (project in file("modules/layered-domain"))
  .dependsOn(layeredInfrastructure % "test->test;test->compile;compile->compile")
  .settings(commonSettings: _*)

lazy val layeredApplication = (project in file("modules/layered-application"))
  .enablePlugins(PlayScala)
  .dependsOn(
    layeredDomain % "test->test;test->compile;compile->compile",
    layeredInfrastructure % "test->test;compile->compile")
  .settings(commonSettings: _*)
  .settings(
    // QueryPathBinderを使う為に以下をroutesにインポート
    routesImport += "com.tsukaby.c_antenna.controller.Implicits._"
  )

lazy val root = (project in file("."))
  .aggregate(layeredApplication, layeredDomain, layeredInfrastructure)
  .dependsOn(layeredApplication, layeredDomain, layeredInfrastructure)
  .enablePlugins(PlayScala)
  .settings(commonSettings: _*)
  .settings(
    name := "c-antenna",
    mainClass in assembly := Some("com.tsukaby.c_antenna.Main"),
    assemblyMergeStrategy in assembly := {
      case "META-INF/MANIFEST.MF" => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )

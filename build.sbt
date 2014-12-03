import play.PlayScala
import play.PlayImport.PlayKeys._

scalaVersion in ThisBuild := "2.11.4"

name := "c-antenna"

version := "1.0"

conflictWarning := ConflictWarning.disable

lazy val root = (project in file("."))
  .aggregate(layeredApplication, layeredDomain, layeredInfrastructure)
  .dependsOn(layeredApplication, layeredDomain, layeredInfrastructure)
  .enablePlugins(PlayScala)
  .settings(
    doc in Compile <<= target.map(_ / "none")
  )

lazy val layeredApplication = (project in file("modules/layered-application"))
  .enablePlugins(PlayScala)
  .dependsOn(
    layeredDomain % "test->test;test->compile;compile->compile",
    layeredInfrastructure % "test->test;compile->compile")
  .settings(
    // QueryPathBinderを使う為に以下をroutesにインポート
    routesImport += "com.tsukaby.c_antenna.controller.Implicits._"
  )

lazy val layeredDomain = (project in file("modules/layered-domain"))
  .dependsOn(layeredInfrastructure % "test->test;test->compile;compile->compile")

lazy val layeredInfrastructure = project in file("modules/layered-infrastructure")
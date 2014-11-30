import play.PlayScala
import play.PlayImport.PlayKeys._

scalaVersion := "2.11.4"

name := "c-antenna"

version := "1.0"

conflictWarning := ConflictWarning.disable

javaOptions in Test += "-Dconfig.file=conf/test.conf"

lazy val root = (project in file("."))
  .aggregate(layeredApplication, layeredDomain, layeredInfrastructure)
  .dependsOn(layeredApplication, layeredDomain, layeredInfrastructure).enablePlugins(PlayScala)

lazy val layeredApplication = (project in file("modules/layered-application"))
  .enablePlugins(PlayScala)
  .dependsOn(layeredDomain, layeredInfrastructure).settings(
    // QueryPathBinderを使う為に以下をroutesにインポート
    routesImport += "com.tsukaby.c_antenna.controller.Implicits._"
  )

lazy val layeredDomain = (project in file("modules/layered-domain"))
  .dependsOn(layeredInfrastructure)

lazy val layeredInfrastructure = project in file("modules/layered-infrastructure")
import play.PlayScala
import sbt.Keys._

//playRunHooks <+= baseDirectory.map(base => Grunt(base))

unmanagedResourceDirectories in Assets += baseDirectory.value / "ui"

excludeFilter in Assets := "*.ts" || "scss" || "*.map" || "test" || "typings"

name := """LayeredApplication"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.33",
  "org.scalikejdbc" %% "scalikejdbc" % "2.2.+",
  "org.scalikejdbc" %% "scalikejdbc-config" % "2.2.+",
  "org.scalikejdbc" %% "scalikejdbc-play-plugin" % "2.3.4",
  "org.scalikejdbc" %% "scalikejdbc-play-fixture-plugin" % "2.3.4",
  "org.scalikejdbc" %% "scalikejdbc-test" % "2.2.+" % "test"
)

lazy val layeredDomain = (project in file("../layered-domain")).enablePlugins(PlayScala)

lazy val layeredInfrastructure = project in file("../layered-infrastructure")

lazy val layeredApplication = (project in file("."))
  .enablePlugins(PlayScala)
  .dependsOn(layeredDomain, layeredInfrastructure)

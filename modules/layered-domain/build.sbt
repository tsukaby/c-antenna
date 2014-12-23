import sbt.Keys._

lazy val commonSettings = Seq(
  version := "1.0",
  organization := "com.tsukaby",
  scalaVersion in ThisBuild := "2.11.4",
  scalacOptions += "-feature",
  javaOptions in Test += "-Dconfig.file=application.conf",
  test in assembly := {},
  doc in Compile <<= target.map(_ / "none"),
  assemblyMergeStrategy in assembly := {
    case "META-INF/MANIFEST.MF" => MergeStrategy.discard
    case x => MergeStrategy.first
  }
)

lazy val layeredInfrastructure = (project in file("../layered-infrastructure"))
  .settings(commonSettings: _*)

lazy val layeredDomain = (project in file("."))
  .dependsOn(layeredInfrastructure)
  .settings(commonSettings: _*)
  .settings(
    name := "layered-domain"
  )

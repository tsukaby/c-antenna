import sbt.Keys._

//playRunHooks <+= baseDirectory.map(base => Grunt(base))

unmanagedResourceDirectories in Assets += baseDirectory.value / "ui"

excludeFilter in Assets := "*.ts" || "scss" || "*.map" || "test" || "typings"

name := """LayeredApplication"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.4"

javaOptions in Test += "-Dconfig.file=conf/test.conf"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.33",
  "org.scalikejdbc" %% "scalikejdbc" % "2.2.+",
  "org.scalikejdbc" %% "scalikejdbc-config" % "2.2.+",
  "org.scalikejdbc" %% "scalikejdbc-play-plugin" % "2.3.4",
  "org.scalikejdbc" %% "scalikejdbc-play-fixture-plugin" % "2.3.4",
  "org.scalikejdbc" %% "scalikejdbc-test" % "2.2.+" % "test",
  "io.spray" %%  "spray-json" % "1.3.1"
)

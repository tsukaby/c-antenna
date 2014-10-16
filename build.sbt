import play.PlayImport.PlayKeys.playRunHooks

scalaVersion := "2.11.2"

name := "c-antenna"

version := "1.0"

javaOptions in Test += "-Dconfig.file=conf/test.conf"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.33",
  "org.scalikejdbc" %% "scalikejdbc" % "2.1.2",
  "org.scalikejdbc" %% "scalikejdbc-config" % "2.1.2",
  "org.scalikejdbc" %% "scalikejdbc-play-plugin" % "2.3.2",
  "org.scalikejdbc" %% "scalikejdbc-play-fixture-plugin" % "2.3.2",
  "org.scalikejdbc" %% "scalikejdbc-test" % "2.1.2" % "test",
  "com.h2database" % "h2" % "1.4.181" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "informa" % "informa" % "0.6.0", //RSS取得
  "redis.clients" % "jedis" % "2.6.0", //Redis
  "biz.source_code" % "base64coder" % "2010-12-19", //Redisへオブジェクト格納用
  "com.github.detro.ghostdriver" % "phantomjsdriver" % "1.1.0" // 画面キャプチャ用
)

scalikejdbcSettings

playRunHooks <+= baseDirectory.map(base => Grunt(base))

lazy val root = (project in file(".")).settings(
  unmanagedResourceDirectories in Assets += baseDirectory.value / "ui",
  excludeFilter in Assets := "*.ts" || "scss" || "*.map" || "test" || "typings"
).enablePlugins(PlayScala)

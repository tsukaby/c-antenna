import play.PlayImport.PlayKeys.playRunHooks
import play.PlayScala

scalaVersion := "2.11.2"

name := "c-antenna"

version := "1.0"

conflictWarning := ConflictWarning.disable

javaOptions in Test += "-Dconfig.file=conf/test.conf"

resolvers += "ATILIKA dependencies" at "http://www.atilika.org/nexus/content/repositories/atilika"

resolvers += "Sedis Repo" at "http://pk11-scratch.googlecode.com/svn/trunk"

resolvers += "Akka-Quartz Repo" at "http://repo.theatr.us"

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
  "com.github.detro.ghostdriver" % "phantomjsdriver" % "1.1.0", // 画面キャプチャ用
  "org.atilika.kuromoji" % "kuromoji" % "0.7.7", // 形態素解析用
  "com.typesafe.akka" %% "akka-actor" % "2.3.2", // batch用
  "io.spray" %% "spray-client" % "1.3.1", // 軽量HTTPクライアント 他のライブラリを使うまでもない部分で使う
  "org.scalaz" %% "scalaz-core" % "7.0.6", // より良い構文のため
  "us.theatr" %% "akka-quartz" % "0.3.0", // cron形式でジョブ登録・実行するためのもの
  "com.github.nscala-time" %% "nscala-time" % "1.4.0" // 日付用
)

scalikejdbcSettings

playRunHooks <+= baseDirectory.map(base => Grunt(base))

lazy val root = (project in file(".")).settings(
  unmanagedResourceDirectories in Assets += baseDirectory.value / "ui",
  excludeFilter in Assets := "*.ts" || "scss" || "*.map" || "test" || "typings"
).enablePlugins(PlayScala)

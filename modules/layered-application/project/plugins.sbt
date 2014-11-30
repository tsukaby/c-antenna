logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.33"
)

addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "2.2.+")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.6")

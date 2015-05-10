logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.35"
)

addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "2.2.4")

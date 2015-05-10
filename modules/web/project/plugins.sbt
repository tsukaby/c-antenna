logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.35"
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.9")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.12.0")

addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "2.2.4")

// dependencyUpdates
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.8")

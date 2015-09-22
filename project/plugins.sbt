logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.36"
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.2")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.12.0")

addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "2.2.4")

// dependencyUpdates
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.8")

// For sbt run
addSbtPlugin("com.typesafe.sbt" % "sbt-aspectj" % "0.10.3")

// For play
addSbtPlugin("io.kamon" % "aspectj-play-runner" % "0.1.2")

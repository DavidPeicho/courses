name := "Spark Project"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies +=  "com.typesafe.play" %% "play-json" % "2.3.0"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.0.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6"

resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
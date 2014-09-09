name := """Jeopardy"""

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.0.6",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.4" % "test",
  "com.github.detro.ghostdriver" % "phantomjsdriver" % "1.1.0" % "test",
  "org.seleniumhq.selenium" % "selenium-support" % "2.42.2" % "test",
  "org.seleniumhq.selenium" % "selenium-server" % "2.42.2" % "test",
  "org.seleniumhq.selenium" % "selenium-java" % "2.42.2" % "test"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

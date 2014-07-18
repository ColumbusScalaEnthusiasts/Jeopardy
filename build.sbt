name := """reactive-orientation"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.0.6",
  "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test",
  "com.github.detro.ghostdriver" % "phantomjsdriver" % "1.1.0" % "test"
)

emberJsVersion := "1.5.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

//testOptions in Test := Nil

name := """Jeopardy"""

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.0.6",
  "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

//testOptions in Test := Nil

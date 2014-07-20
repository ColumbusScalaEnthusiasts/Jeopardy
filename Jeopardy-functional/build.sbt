name := """Jeopardy-functional"""

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.11",
  "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test",
  "org.seleniumhq.selenium" % "selenium-server" % "2.42.2" % "test"
)

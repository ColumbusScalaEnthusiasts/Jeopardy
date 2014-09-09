import Dependencies._

name := """Jeopardy"""

version in ThisBuild := "0.1-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.2"

lazy val game = (project in file("game")
  settings (libraryDependencies ++= gameDependencies)
  ).enablePlugins(PlayScala)

lazy val functional = (project in file("functional")
  settings (libraryDependencies ++= functionalDependencies)
  )

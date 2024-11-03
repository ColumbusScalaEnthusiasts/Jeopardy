import Dependencies._

name := """Jeopardy"""

ThisBuild / version := "0.1-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.1"

lazy val game = (project in file("game")
  settings (libraryDependencies ++= (guice +: gameDependencies)))
  .enablePlugins(PlayScala)

lazy val functional = (project in file("functional")
  settings (libraryDependencies ++= functionalDependencies))

ThisBuild / scalacOptions += "-Wunused:all"
inThisBuild(
  List(
    scalaVersion := "3.6.1",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision
  )
)

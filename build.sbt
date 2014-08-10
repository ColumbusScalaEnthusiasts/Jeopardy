import dependencies._

name := "Jeopardy"

scalaVersion in ThisBuild := "2.11.1"

lazy val ui = (project in file("ui")
    settings(libraryDependencies ++= uiDependencies)
  ).enablePlugins(PlayScala)

lazy val functional = (project in file("functional")
    settings(libraryDependencies ++= functionalDependencies)
  )

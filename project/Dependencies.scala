import sbt._
import Keys._

object Dependencies {

  val scalaz = "org.scalaz" %% "scalaz-core" % "7.0.6"
  val scalaTest = "org.scalatest" % "scalatest_2.11" % "2.2.0"
  val akkaTestKit = "com.typesafe.akka" % "akka-testkit_2.11" % "2.3.4"
  val phantomJs = "com.github.detro.ghostdriver" % "phantomjsdriver" % "1.1.0"
  val junit = "junit" % "junit" % "4.11"
  val seleniumServer = "org.seleniumhq.selenium" % "selenium-server" % "2.42.2"
  val seleniumSupport = "org.seleniumhq.selenium" % "selenium-support" % "2.42.2"
  val seleniumJava = "org.seleniumhq.selenium" % "selenium-java" % "2.42.2"

  val gameDependencies = Seq(
    scalaz,
    scalaTest % "test",
    akkaTestKit % "test",
    phantomJs % "test",
    seleniumServer % "test",
    seleniumSupport % "test",
    seleniumJava % "test"
  )
  val functionalDependencies = Seq(scalaTest % "test", junit, seleniumServer % "test")
}

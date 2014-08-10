import sbt._
import Keys._

object dependencies {

  private val scalaz = "org.scalaz" %% "scalaz-core" % "7.0.6"
  private val scalaTest = "org.scalatest" % "scalatest_2.11" % "2.2.0"
  private val akkaTestKit = "com.typesafe.akka" % "akka-testkit_2.11" % "2.3.4"
  private val phantomJsDriver = "com.github.detro.ghostdriver" % "phantomjsdriver" % "1.1.0"
  private val jUnit = "junit" % "junit" % "4.11"
  private val seleniumServer = "org.seleniumhq.selenium" % "selenium-server" % "2.42.2"

  val uiDependencies = Seq(
    scalaz,
    scalaTest % "test",
    akkaTestKit % "test",
    phantomJsDriver % "test"
  )

  val functionalDependencies = Seq(
    jUnit,
    scalaTest % "test",
    seleniumServer % "test"
  )

}
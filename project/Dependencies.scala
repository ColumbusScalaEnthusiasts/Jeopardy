import sbt._

object Dependencies {

  val seleniumVersion = "2.43.1"

  val scalaz = "org.scalaz" %% "scalaz-core" % "7.1.0"
  val scalaTest = "org.scalatest" % "scalatest_2.11" % "2.2.1"
  val akkaTestKit = "com.typesafe.akka" % "akka-testkit_2.11" % "2.3.6"
  val phantomJs = "com.github.detro.ghostdriver" % "phantomjsdriver" % "1.1.0"
  val junit = "junit" % "junit" % "4.11"
  val seleniumServer = "org.seleniumhq.selenium" % "selenium-server" % seleniumVersion
  val seleniumSupport = "org.seleniumhq.selenium" % "selenium-support" % seleniumVersion
  val seleniumJava = "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion

  val gameDependencies = Seq(
    scalaz,
    scalaTest % "test",
    akkaTestKit % "test",
    phantomJs % "test",
    seleniumServer % "test",
    seleniumSupport % "test",
    seleniumJava % "test"
  )
  val functionalDependencies = Seq(
    scalaTest % "test",
    phantomJs % "test",
    junit,
    seleniumServer % "test"
  )
}

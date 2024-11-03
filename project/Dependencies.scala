import sbt._

object Dependencies {

  object Versions {
    val selenium = "4.26.0"
    val seleniumServer = "3.141.59"
    val pekkoTest =
      "1.0.3" // this has to manually match play's.
  }

  val scalaTest = "org.scalatest" %% "scalatest" % "3.2.19" // "2.2.1"
  val scalaTestPlus = "org.scalatestplus" %% "junit-4-13" % "3.2.19.0"

  val pekkoTestkit = "org.apache.pekko" %% "pekko-testkit" % Versions.pekkoTest
  val phantomJs = "com.codeborne" % "phantomjsdriver" % "1.5.0"
  val junit = "junit" % "junit" % "4.13.2"
  val seleniumServer =
    "org.seleniumhq.selenium" % "selenium-server" % Versions.seleniumServer
  val seleniumSupport =
    "org.seleniumhq.selenium" % "selenium-support" % Versions.selenium
  val seleniumJava =
    "org.seleniumhq.selenium" % "selenium-java" % Versions.selenium
  val testPlusPlay =
    "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1"
  val scalatestJunit = "org.scalatestplus" %% "junit-4-13" % "3.2.19.0"
  val commonsIo = "commons-io" % "commons-io" % "2.17.0"

  val gameDependencies = Seq(
    scalaTestPlus,
    phantomJs,
    seleniumServer,
    seleniumSupport,
    seleniumJava,
    testPlusPlay,
    pekkoTestkit,
    scalatestJunit
  ).map(_ % Test)

  val functionalDependencies =
    junit +: Seq(
      scalaTest,
      scalatestJunit,
      phantomJs,
      seleniumServer,
      pekkoTestkit,
      commonsIo
    ).map(
      _ % Test
    )
}

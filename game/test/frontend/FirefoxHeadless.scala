package frontend

import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.scalatestplus.selenium.Driver
import org.scalatestplus.selenium.WebBrowser

trait FirefoxHeadless extends WebBrowser with Driver {

  implicit val webDriver: WebDriver = new FirefoxDriver(
    new FirefoxOptions().addArguments("--headless")
  )

  /** Captures a screenshot and saves it as a file in the specified directory.
    */
  def captureScreenshot(directory: String): Unit = {
    capture to directory
  }

}

object FirefoxHeadlessDriver extends FirefoxHeadless

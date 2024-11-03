package frontend

import org.openqa.selenium.WebDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.scalatestplus.selenium.Driver
import org.scalatestplus.selenium.WebBrowser

trait GhostDriver extends WebBrowser with Driver {
  implicit val webDriver: WebDriver = new PhantomJSDriver()

  /** Captures a screenshot and saves it as a file in the specified directory.
    */
  def captureScreenshot(directory: String): Unit = {
    capture to directory
  }

}

object GhostDriver extends GhostDriver

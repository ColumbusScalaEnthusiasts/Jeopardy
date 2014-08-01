package frontend

import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.scalatest.selenium.{WebBrowser, Driver}

trait PhantomJS extends WebBrowser with Driver {
  implicit val webDriver = new PhantomJSDriver()

  /**
   * Captures a screenshot and saves it as a file in the specified directory.
   */
  def captureScreenshot(directory: String) {
    capture to directory
  }

}

object PhantomJS extends PhantomJS

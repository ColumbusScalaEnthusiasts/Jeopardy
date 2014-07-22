package specs

import org.openqa.selenium.WebDriver

case class ContextPackage (
  driver: WebDriver,
  baseUrl: String
) {
  def close () {
    driver.close ()
  }
}
package specs

import java.util.concurrent.TimeUnit
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.scalatest.Args
import org.scalatest.BeforeAndAfterEach
import org.scalatest.Status
import org.scalatest.Suite
import org.scalatest.path.FunSpec
import org.openqa.selenium.ie.InternetExplorerDriver

trait JeopardyFunctional {
  private var _driver: WebDriver = null  
  implicit protected def driver = _driver
  
  def functional (body: (() => Unit)) {
    _driver = makeDriver ()
    _driver.manage ().timeouts ().implicitlyWait (60, TimeUnit.SECONDS);
    try {
      body ()
    }
    finally {
      _driver.close ()
    }
  }
  
  private def makeDriver (): WebDriver = {
    val driverType = System.getProperty ("webDriverType", "FIREFOX")
    driverType.charAt (0).toUpper match {
      case 'F' => new FirefoxDriver ()
      case 'C' => new ChromeDriver ()
      case 'H' => new HtmlUnitDriver ()
      case 'I' => new InternetExplorerDriver ()
      case _ => throw new IllegalArgumentException (s"Unknown webDriverType: ${driverType}")
    }
  }
}

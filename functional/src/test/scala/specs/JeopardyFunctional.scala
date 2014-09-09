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
  private var _context: ContextPackage = null
  implicit protected def context = _context
  
  def functional (body: (() => Unit)) {
    _context = makeAnotherContext ()
    try {
      body ()
    }
    finally {
      _context.close ()
    }
  }
  
  def makeAnotherContext () = ContextPackage (makeDriver (), getBaseUrl ())
  
  private def makeDriver (): WebDriver = {
    val driverType = System.getProperty ("webDriverType", "FIREFOX")
    val driver = driverType.charAt (0).toUpper match {
      case 'F' => new FirefoxDriver ()
      case 'C' => new ChromeDriver ()
      case 'H' => new HtmlUnitDriver ()
      case 'I' => new InternetExplorerDriver ()
      case _ => throw new IllegalArgumentException (s"Unknown webDriverType: ${driverType}")
    }
    driver.manage ().timeouts ().implicitlyWait (10, TimeUnit.SECONDS);
    driver
  }
  
  private def getBaseUrl (): String = {
    System.getProperty ("baseUrl", "http://localhost:9000")
  }
}

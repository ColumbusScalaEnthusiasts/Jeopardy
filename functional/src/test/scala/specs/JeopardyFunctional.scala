package specs

import java.util.concurrent.TimeUnit
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver

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

  def makeAnotherContext() = ContextPackage(makeDriver(), getBaseUrl())

  private def makeDriver(): WebDriver = {
    // DO: Set environment variables for webdriver binaries
    //System.setProperty("webdriver.chrome.driver", "c:/bin/chromedriver.exe")
    //System.setProperty("webdriver.ie.driver", "c:/bin/iedriverserver.exe")
    //System.setProperty("phantomjs.binary.path", "c:/bin/phantomjs/phantomjs.exe")

    val driverType = System.getProperty("webDriverType", "P")
    val driver = driverType.charAt(0).toUpper match {
      case 'F' => new FirefoxDriver()
      case 'C' => new ChromeDriver()
      case 'H' => new HtmlUnitDriver()
      case 'I' => new InternetExplorerDriver()
      case 'P' => new PhantomJSDriver()
      case _ => throw new IllegalArgumentException(s"Unknown webDriverType: ${driverType}")
    }
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    driver
  }

  private def getBaseUrl(): String = {
    System.getProperty("baseUrl", "http://localhost:9000")
  }
}

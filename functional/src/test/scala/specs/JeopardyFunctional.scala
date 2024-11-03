package specs

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver

import java.util.concurrent.TimeUnit

trait JeopardyFunctional {
  private var _context: ContextPackage = null
  implicit protected def context: ContextPackage = _context

  def functional(body: (() => Unit)): Unit = {
    _context = makeAnotherContext()
    try {
      body()
    } finally {
      _context.close()
    }
  }

  def makeAnotherContext() = ContextPackage(makeDriver(), getBaseUrl())

  private def makeDriver(): WebDriver = {
    val driverType: String = System.getProperty("webDriverType", "CH")
    val driver = driverType.toUpperCase match {
      case "F" => new FirefoxDriver()
      case "FH" => {
        val profile = new FirefoxProfile()
        val opts = new FirefoxOptions()
        opts.addArguments("--headless")
        opts.addArguments("--new-instance")
        opts.setProfile(profile)
        new FirefoxDriver(opts)
      }
      case "C" => new ChromeDriver()
      case "CH" => {
        val options = new ChromeOptions()
        options.addArguments("--headless=new")
        new ChromeDriver(options)
      }
      case "I" => new InternetExplorerDriver()
      case "P" => new PhantomJSDriver()
      case _ =>
        throw new IllegalArgumentException(
          s"Unknown webDriverType: $driverType"
        )
    }
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
    driver
  }

  private def getBaseUrl(): String = {
    System.getProperty("baseUrl", "http://localhost:9000")
  }
}

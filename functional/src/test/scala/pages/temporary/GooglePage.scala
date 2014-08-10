package pages.temporary

import pages.Page
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.scalatest.selenium.WebBrowser.Element
import org.openqa.selenium.WebElement
import scala.collection.JavaConverters._
import java.util.concurrent.TimeUnit

class GooglePage extends Page {
  
  def goTo () {
    driver.get ("http://www.google.com")
  }
  
  def setSearch (searchString: String) {
    driver.findElement (By.id ("gbqfq")).sendKeys(searchString)
  }
  
  def doSearch () {
    driver.findElement (By.id ("gbqfb")).click ();
  }
  
  class ResultSubPage (parent: WebElement) {
    def linkName: String = parent.findElement (By.xpath ("""//h3[@class="r"]/a""")).getText ();
    def linkUrl: String = parent.findElement (By.xpath ("""//h3[@class="r"]/a""")).getAttribute ("href");
  }
  
  def results: List[ResultSubPage] = {
    driver.findElement (By.xpath ("""//td[@class="cur"]"""))
    driver.findElements (By.xpath ("""//li[@class="g"]""")).asScala.toList.map {new ResultSubPage (_)}
  }
}

package pages

import org.openqa.selenium.WebDriver
import specs.ContextPackage
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.StaleElementReferenceException
import scala.collection.JavaConverters._
import org.openqa.selenium.SearchContext

object Page {
  def apply[T <: Page] (cls: Class[T])(implicit context: ContextPackage): T = {
    val page = cls.newInstance ().asInstanceOf[T]
    page.context = context;
    return page;
  }
}

trait Page {
  private var context: ContextPackage = null
  implicit protected def driver = context.driver
  protected def baseUrl = context.baseUrl
  
  def isDisplayed: Boolean = throw new UnsupportedOperationException ()
    
  def click (context: SearchContext, selector: By): Unit = {
    findElement (context, selector) {element => element.click ()}
  }
  
  def click (selector: By): Unit = click (driver, selector)
  
  def sendKeys (context: SearchContext, selector: By, keys: String): Unit = {
    findElement (context, selector) {element => element.sendKeys (keys)}
  }
  
  def sendKeys (selector: By, keys: String): Unit = sendKeys (driver, selector, keys)
  
  def text (context: SearchContext, selector: By): String = {
    findElement (context, selector) {element => element.getText ()}
  }
  
  def text (selector: By): String = text (driver, selector)
  
  def isDisplayed (context: SearchContext, selector: By): Boolean = {
    findElement (context, selector) {element => element.isDisplayed ()}
  }
  
  def isDisplayed (selector: By): Boolean = isDisplayed (driver, selector)

  protected def findElement[T] (context: SearchContext, selector: By) (action: WebElement => T): T = {
    keepTrying ((context, selector)){case (c, s) => {
      val objective = c.findElement (s)
      action (objective)
    }}
  }
  
  protected def findElement[T] (selector: By) (action: WebElement => T): T = {
    findElement (driver, selector) (action)
  }
  
  protected def findElements[T] (context: SearchContext, selector: By) (action: List[WebElement] => T): T = {
    keepTrying ((context, selector)){case (c, s) => {
      val objectives = c.findElements (s).asScala.toList
      action (objectives)
    }}
  }
  
  protected def findElements[T] (selector: By) (action: List[WebElement] => T): T = {
    findElements (driver, selector) (action)
  }
  
  private def keepTrying[P, Q] (x: P)(f: P => Q): Q = {
    try {
      f (x)
    }
    catch {
      case e: StaleElementReferenceException => keepTrying (x)(f)
    }
  }
}

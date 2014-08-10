package pages

import org.openqa.selenium.WebDriver
import specs.ContextPackage

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
}

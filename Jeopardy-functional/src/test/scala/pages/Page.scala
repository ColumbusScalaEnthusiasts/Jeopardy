package pages

import org.openqa.selenium.WebDriver

object Page {
  def apply[T <: Page] (cls: Class[T])(implicit driver: WebDriver): T = {
    val page = cls.newInstance ().asInstanceOf[T]
    page._driver = driver
    return page;
  }
}

trait Page {
  private var _driver: WebDriver = null
  implicit protected def driver = _driver
}

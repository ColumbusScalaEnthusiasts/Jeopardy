package pages.board

import pages.Page
import org.openqa.selenium.By

class BoardPage extends Page {
  override def isDisplayed(): Boolean = isDisplayed (By.id ("board-page-content"))
}

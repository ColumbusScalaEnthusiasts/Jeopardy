package model.board

import specs.ContextPackage
import pages.Page
import pages.board.BoardPage

class Board (implicit context: ContextPackage) {
  
  def isDisplayed(): Boolean = {
    val page = Page (classOf[BoardPage])
    return page.isDisplayed ()
  }
}
package model.board

import model.ScreenWithListedPlayers
import pages.Page
import pages.board.BoardPage
import pages.vestibule.Player
import specs.ContextPackage

class Board(implicit context: ContextPackage) extends ScreenWithListedPlayers {
  def isDisplayed(): Boolean = {
    val page = Page(classOf[BoardPage])
    return page.isDisplayed
  }

  def userInfo: Player = {
    Page(classOf[BoardPage]).userInfo
  }

  override def playersPresent: List[Player] = {
    Page(classOf[BoardPage]).playersPresent
  }
}

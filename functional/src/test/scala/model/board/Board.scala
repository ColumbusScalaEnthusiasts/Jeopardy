package model.board

import specs.ContextPackage
import pages.Page
import pages.board.BoardPage
import model.ScreenWithListedPlayers
import pages.vestibule.Player

class Board (implicit context: ContextPackage) extends ScreenWithListedPlayers {
  
  def isDisplayed(): Boolean = {
    val page = Page (classOf[BoardPage])
    return page.isDisplayed ()
  }
  
  def userInfo: Player = {
    Page (classOf[BoardPage]).userInfo
  }
    
  override def playersPresent: List[Player] = {
    Page (classOf[BoardPage]).playersPresent
  }
}

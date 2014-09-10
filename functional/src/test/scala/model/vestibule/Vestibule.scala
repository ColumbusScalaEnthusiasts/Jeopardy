package model.vestibule

import specs.ContextPackage
import pages.vestibule.VestibulePage
import pages.vestibule.Player
import pages.Page
import model.ScreenWithListedPlayers

class Vestibule (implicit context: ContextPackage) extends ScreenWithListedPlayers {
  
  def enterViaIndex () {
    Page (classOf[VestibulePage]).enterViaIndex ()
  }
  
  def enterDirectly () {
    Page (classOf[VestibulePage]).enterDirectly ()
  }
  
  def signIn (name: String) {
    val page = Page (classOf[VestibulePage])
    page.setPlayerName (name)
    page.signIn ()
  }
  
  def ready () {
    val page = Page (classOf[VestibulePage])
    page.ready ()
  }
  
  def start () {
    val page = Page (classOf[VestibulePage])
    page.start ()
  }
  
  def signOut () {
    val page = Page (classOf[VestibulePage])
    page.signOut ()
  }
  
  override def playersPresent: List[Player] = {
    Page (classOf[VestibulePage]).playersPresent
  }
}

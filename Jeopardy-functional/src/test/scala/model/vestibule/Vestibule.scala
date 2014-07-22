package model.vestibule

import specs.ContextPackage
import pages.vestibule.VestibulePage
import pages.vestibule.Player
import pages.Page

class Vestibule (implicit context: ContextPackage) {

  def enterViaIndex () {
    Page (classOf[VestibulePage]).enterViaIndex ()
  }
  
  def enterDirectly () {
    Page (classOf[VestibulePage]).enterDirectly ()
  }
  
  def signInPlayer (name: String) {
    val page = Page (classOf[VestibulePage])
    page.setPlayerName (name)
    page.signIn ()
  }
  
  def playersPresent: List[Player] = {
    Page (classOf[VestibulePage]).playersPresent
  }
}

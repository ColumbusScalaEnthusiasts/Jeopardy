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
  
  def signIn (name: String) {
    val page = Page (classOf[VestibulePage])
    page.setPlayerName (name)
    page.signIn ()
  }
  
  def signOut () {
    val page = Page (classOf[VestibulePage])
    page.signOut ()
  }
  
  def playersPresent: List[Player] = {
    Page (classOf[VestibulePage]).playersPresent
  }
}

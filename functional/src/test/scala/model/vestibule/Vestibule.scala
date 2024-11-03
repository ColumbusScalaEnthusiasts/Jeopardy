package model.vestibule

import model.ScreenWithListedPlayers
import pages.Page
import pages.vestibule.Player
import pages.vestibule.VestibulePage
import specs.ContextPackage

class Vestibule(implicit context: ContextPackage)
    extends ScreenWithListedPlayers {

  def enterViaIndex(): Unit = {
    Page(classOf[VestibulePage]).enterViaIndex()
  }

  def enterDirectly(): Unit = {
    Page(classOf[VestibulePage]).enterDirectly()
  }

  def signIn(name: String): Unit = {
    val page = Page(classOf[VestibulePage])
    page.setPlayerName(name)
    page.signIn()
  }

  def ready(): Unit = {
    val page = Page(classOf[VestibulePage])
    page.ready()
  }

  def start(): Unit = {
    val page = Page(classOf[VestibulePage])
    page.start()
  }

  def signOut(): Unit = {
    val page = Page(classOf[VestibulePage])
    page.signOut()
  }

  override def playersPresent: List[Player] = {
    Page(classOf[VestibulePage]).playersPresent
  }
}

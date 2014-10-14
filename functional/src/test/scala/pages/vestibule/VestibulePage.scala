package pages.vestibule

import pages.Page
import org.openqa.selenium.By
import scala.collection.JavaConverters._

class VestibulePage extends Page {
  def enterViaIndex () {
    driver.get (baseUrl + "/")
  }

  def enterDirectly () {
    driver.get (baseUrl + "/vestibule")
  }

  def setPlayerName (name: String) {
    sendKeys (By.id ("player-name"), name)
  }

  def signIn () {
    click (By.id ("sign-in-button"))
  }

  def ready () {
    click (By.id ("ready-button"))
  }

  def start () {
    click (By.id ("start-button"))
  }

  def signOut () {
    click (By.id ("sign-out-button"))
  }

  def playersPresent: List[Player] = {
    findElements (By.xpath ("""//tr[@class="player-info"]""")) {_.map {info =>
      val regMatch = """player-info-(\d+)$""".r.findFirstMatchIn(info.getAttribute ("id")).get
      val id = regMatch.group (1)
      Player (
        text (By.id (s"player-name-${id}")),
        text (By.id (s"player-status-${id}"))
      )
    }}
  }
}

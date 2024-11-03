package pages.vestibule

import org.openqa.selenium.By
import pages.Page

class VestibulePage extends Page {
  def enterViaIndex(): Unit = {
    driver.get(baseUrl + "/")
  }

  def enterDirectly(): Unit = {
    driver.get(baseUrl + "/vestibule")
  }

  def setPlayerName(name: String): Unit = {
    sendKeys(By.id("player-name"), name)
  }

  def signIn(): Unit = {
    click(By.id("sign-in-button"))
  }

  def ready(): Unit = {
    click(By.id("ready-button"))
  }

  def start(): Unit = {
    click(By.id("start-button"))
  }

  def signOut(): Unit = {
    click(By.id("sign-out-button"))
  }

  def playersPresent: List[Player] = {
    findElements(By.xpath("""//tr[@class="player-info"]""")) {
      _.map { info =>
        val regMatch = """player-info-(\d+)$""".r
          .findFirstMatchIn(info.getAttribute("id"))
          .get
        val id = regMatch.group(1)
        Player(
          text(By.id(s"player-name-${id}")),
          text(By.id(s"player-status-${id}"))
        )
      }
    }
  }
}

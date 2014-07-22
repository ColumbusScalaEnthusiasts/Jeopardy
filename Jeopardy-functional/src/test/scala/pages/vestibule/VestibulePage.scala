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
    val nameField = driver.findElement (By.id ("player-name"))
    nameField.sendKeys (name)
  }
  
  def signIn () {
    val signInButton = driver.findElement (By.id ("sign-in"))
    signInButton.click ();
  }
  
  def playersPresent: List[Player] = {
    val playerInfo = driver.findElements (By.xpath ("""//li[@class="player-info"]""")).asScala
    playerInfo.map {info =>
      val regMatch = """player-info-(\d+)$""".r.findFirstMatchIn(info.getAttribute ("id")).get
      val id = regMatch.group (1);
      Player (
        driver.findElement (By.id (s"player-name-${id}")).getText()
      )
    }.toList
  }
}

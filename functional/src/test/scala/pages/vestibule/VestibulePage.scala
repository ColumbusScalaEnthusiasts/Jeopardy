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
    driver.findElement (By.id ("player-name")).sendKeys (name)
  }
  
  def signIn () {
    driver.findElement (By.id ("sign-in-button")).click ()
  }
  
  def ready () {
    driver.findElement (By.id ("ready-button")).click ()
  }
  
  def signOut () {
    driver.findElement (By.id ("sign-out-button")).click ()
  }
  
  def playersPresent: List[Player] = {
    val playerInfo = driver.findElements (By.xpath ("""//tr[@class="player-info"]""")).asScala.toList
    playerInfo.map {info =>
      val regMatch = """player-info-(\d+)$""".r.findFirstMatchIn(info.getAttribute ("id")).get
      val id = regMatch.group (1);
      Player (
        driver.findElement (By.id (s"player-name-${id}")).getText (),
        driver.findElement (By.id (s"player-status-${id}")).getText ()
      )
    }
  }
}

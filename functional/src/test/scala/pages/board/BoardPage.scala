package pages.board

import pages.Page
import org.openqa.selenium.By
import pages.vestibule.Player

class BoardPage extends Page {
  override def isDisplayed(): Boolean = isDisplayed (By.id ("board-page-content"))

  def userInfo: Player = {
    Player (
      text (By.id ("user-player-name")),
      text (By.id ("user-player-score")).toInt,
      getAttribute (By.id ("user-player-instructions"), "status")
    )
  }

  def playersPresent: List[Player] = {
    findElements (By.className ("opponent-row")) {_.map {row =>
      val regMatch = """opponent-row-(\d+)$""".r.findFirstMatchIn(row.getAttribute ("id")).get
      val id = regMatch.group (1)
      Player (
        text (By.id (s"opponent-name-${id}")),
        text (By.id (s"opponent-score-${id}")).toInt,
        text (By.id (s"opponent-status-${id}"))
      )
    }}
  }
}

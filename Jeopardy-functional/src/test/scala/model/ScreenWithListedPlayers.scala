package model

import pages.vestibule.Player

trait ScreenWithListedPlayers {
  def playersPresent (): List[Player]
}

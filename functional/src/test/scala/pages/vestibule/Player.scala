package pages.vestibule

object Player {
  def apply (name: String, status: String): Player = Player (name, 0, status)
}

case class Player (name: String, score: Int, status: String)

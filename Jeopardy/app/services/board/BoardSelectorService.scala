package services.board

case class Board (
    columns: List[BoardColumn]
)

case class BoardColumn (
		category: Category,
		questions: List[Question]
)
		
case class Category (
		id: Long,
		name: String
)
		
case class Question (
    id: Long,
		text: String,
		value: Int,
		answers: List[Answer]
)

case class Answer (
    id: Long,
    text: String,
    correct: Boolean
)

class BoardSelectorService {
  def makeBoard (multiplier: Int): Board = {
    throw new UnsupportedOperationException ("Test-drive me!")
  }
}
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
    return Board ((1 to 6).map {column =>
      BoardColumn (
        Category (100 + column, s"Category ${column}"),
        (1 to 5).map {row =>
          Question (
            (column * 10) + row,
            s"${row * multiplier}-point question from Category ${column}",
            row * multiplier,
            ((column * row) & 1) match {
              case 0 => List (Answer (1, "Right Answer", true), Answer (2, "Wrong Answer", false))
              case 1 => List (Answer (2, "Wrong Answer", false), Answer (1, "Right Answer", true))
            }
          )
        }.toList
      )
    }.toList)
  }
}
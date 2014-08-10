package services.routerplugins

import play.api.libs.json.JsValue
import actors.ActivePlayerRecord
import actors.ActivePlayerStatus
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsArray
import play.api.libs.json.JsNumber
import actors.ChooseQuestion
import utils.Utils._
import actors.Buzz

case class BoardStatus (
    players: List[ActivePlayerRecord],
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
		value: Int
)

case class AskQuestion (categoryIndex: Int, rowIndex: Int, text: String)

case class QuestionClaimed (buzzWinnerId: Long)

case class BuzzWinner ()

class BoardPlugin extends RouterPlugin {
  
  override def handleIncomingMessage (msgType: String, msgData: JsValue) {
    msgType match {
      case "chooseQuestion" => handleChooseQuestion (intField (msgData, "categoryIndex"), 
          intField (msgData, "rowIndex"))
      case "buzz" => handleBuzz ()
    }
  }
  
  override def handleOutgoingMessage (msg: Any) {
    msg match {
      case msg: BoardStatus => handleBoardStatus (msg)
      case msg: AskQuestion => handleAskQuestion (msg)
      case msg: QuestionClaimed => handleQuestionClaimed (msg)
      case msg: BuzzWinner => handleBuzzWinner ()
    }
  }
  
  private def handleChooseQuestion (categoryIndex: Int, rowIndex: Int) {
    send (ChooseQuestion (categoryIndex, rowIndex), backEndHandler)
  }
  
  private def handleBuzz () {
    send (Buzz (), backEndHandler)
  }
  
  private def handleBoardStatus (msg: BoardStatus) {
    val json = new JsObject (List (
      ("type", new JsString ("boardStatus")),
      ("data", new JsObject (List (
        ("players", new JsArray (msg.players.map {player =>
          new JsObject (List (
            ("id", new JsNumber (player.id)),
            ("name", JsString (player.name)),
            ("score", JsNumber (player.score)),
            ("status", JsString (player.status.getClass ().getSimpleName ().replace("$", "")))
          ))
        })),
        ("columns", new JsArray (msg.columns.map {column =>
          new JsObject (List (
            ("category", new JsObject (List (
              ("id", new JsNumber (column.category.id)), 
              ("name", new JsString (column.category.name))
            ))),
            ("questions", new JsArray (column.questions.map {question =>
              new JsObject (List (
                ("id", new JsNumber (question.id)),
                ("value", new JsNumber (question.value))
              ))
            }))
          ))
        }))
      )))
    ))
    send (json, outputSocket)
  }
  
  private def handleAskQuestion (msg: AskQuestion) {
    val json = new JsObject (List (
      ("type", new JsString ("askQuestion")),
      ("data", new JsObject (List (
        ("categoryIndex", new JsNumber (msg.categoryIndex)),
        ("rowIndex", new JsNumber (msg.rowIndex)),
        ("text", new JsString (msg.text))
      )))
    ))
    send (json, outputSocket)
  }
  
  private def handleQuestionClaimed (msg: QuestionClaimed) {
    val json = new JsObject (List (
      ("type", new JsString ("questionClaimed")),
      ("data", new JsObject (List (
        ("buzzWinnerId", new JsNumber (msg.buzzWinnerId))
      )))
    ))
    send (json, outputSocket)
  }
  
  private def handleBuzzWinner () {
    val json = new JsObject (List (
      ("type", new JsString ("buzzWinner")),
      ("data", new JsObject (Nil))
    ))
    send (json, outputSocket)
  }
}
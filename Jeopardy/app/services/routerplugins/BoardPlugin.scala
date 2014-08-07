package services.routerplugins

import play.api.libs.json.JsValue
import actors.ActivePlayerStatus
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsArray
import play.api.libs.json.JsNumber

case class BoardStatus (
    playerStatus: ActivePlayerStatus,
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
    
  }
  
  override def handleOutgoingMessage (msg: Any) {
    msg match {
      case msg: BoardStatus => handleBoardStatus (msg)
    }
  }
  
  private def handleBoardStatus (msg: BoardStatus) {
    val boardStatus = new JsObject (List (
      ("type", new JsString ("boardStatus")),
      ("data", new JsObject (List (
        ("playerStatus", new JsString (msg.playerStatus.getClass ().getSimpleName ().replace("$", ""))),
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
    send (boardStatus, outputSocket)
  }
}
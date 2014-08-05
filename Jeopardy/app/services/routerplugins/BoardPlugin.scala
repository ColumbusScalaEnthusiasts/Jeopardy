package services.routerplugins

import play.api.libs.json.JsValue
import actors.ActivePlayerStatus

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
    
  }
}
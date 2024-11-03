package services.routerplugins

import actors.ActivePlayerRecord
import actors.Buzz
import actors.ChooseQuestion
import play.api.libs.json.JsArray
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import utils.Utils.*

case class BoardStatus(
    players: List[ActivePlayerRecord],
    columns: List[BoardColumn]
)

case class BoardColumn(
    category: Category,
    questions: List[Question]
)

case class Category(
    id: Long,
    name: String
)

case class Question(
    id: Long,
    value: Int
)

case class AskQuestion(id: Long, text: String)

case class QuestionClaimed(buzzWinnerId: Long)

case class BuzzWinner()

class BoardPlugin extends RouterPlugin {

  override def handleIncomingMessage(
      msgType: String,
      msgData: JsValue
  ): Unit = {
    msgType match {
      case "chooseQuestion" => handleChooseQuestion(longField(msgData, "id"))
      case "buzz"           => handleBuzz()
    }
  }

  override def handleOutgoingMessage(msg: Any): Unit = {
    msg match {
      case msg: BoardStatus     => handleBoardStatus(msg)
      case msg: AskQuestion     => handleAskQuestion(msg)
      case msg: QuestionClaimed => handleQuestionClaimed(msg)
      case msg: BuzzWinner      => handleBuzzWinner()
    }
  }

  private def handleChooseQuestion(id: Long): Unit = {
    send(ChooseQuestion(id), backEndHandler)
  }

  private def handleBuzz(): Unit = {
    send(Buzz(), backEndHandler)
  }

  private def handleBoardStatus(msg: BoardStatus): Unit = {
    val json = new JsObject(
      Map(
        ("type", new JsString("boardStatus")),
        (
          "data",
          new JsObject(
            Map(
              (
                "players",
                new JsArray(msg.players.map { player =>
                  new JsObject(
                    Map(
                      ("id", new JsNumber(player.id)),
                      ("name", JsString(player.name)),
                      ("score", JsNumber(player.score)),
                      (
                        "status",
                        JsString(
                          player.status
                            .getClass()
                            .getSimpleName()
                            .replace("$", "")
                        )
                      )
                    )
                  )
                }.toIndexedSeq)
              ),
              (
                "columns",
                new JsArray(msg.columns.map { column =>
                  new JsObject(
                    Map(
                      (
                        "category",
                        new JsObject(
                          Map(
                            ("id", new JsNumber(column.category.id)),
                            ("name", new JsString(column.category.name))
                          )
                        )
                      ),
                      (
                        "questions",
                        new JsArray(column.questions.map { question =>
                          new JsObject(
                            Map(
                              ("id", new JsNumber(question.id)),
                              ("value", new JsNumber(question.value))
                            )
                          )
                        }.toIndexedSeq)
                      )
                    )
                  )
                }.toIndexedSeq)
              )
            )
          )
        )
      )
    )
    send(json, outputSocket)
  }

  private def handleAskQuestion(msg: AskQuestion): Unit = {
    val json = new JsObject(
      Map(
        ("type", new JsString("askQuestion")),
        (
          "data",
          new JsObject(
            Map(
              ("id", new JsNumber(msg.id)),
              ("text", new JsString(msg.text))
            )
          )
        )
      )
    )
    send(json, outputSocket)
  }

  private def handleQuestionClaimed(msg: QuestionClaimed): Unit = {
    val json = new JsObject(
      Map(
        ("type", new JsString("questionClaimed")),
        (
          "data",
          new JsObject(
            Map(
              ("buzzWinnerId", new JsNumber(msg.buzzWinnerId))
            )
          )
        )
      )
    )
    send(json, outputSocket)
  }

  private def handleBuzzWinner(): Unit = {
    val json = new JsObject(
      Map(
        ("type", new JsString("buzzWinner")),
        ("data", new JsObject(Map()))
      )
    )
    send(json, outputSocket)
  }
}

package actors

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import services.board.Board
import services.board.BoardSelectorService
import services.routerplugins.BoardStatus
import services.routerplugins.AskQuestion
import services.routerplugins.BuzzWinner
import services.routerplugins.QuestionClaimed
import akka.actor.ActorSystem
import akka.actor.Props

trait ActivePlayerStatus {}
case object InControlStatus extends ActivePlayerStatus {}
case object WaitingForChoiceStatus extends ActivePlayerStatus {}
case object WaitingForBuzzStatus extends ActivePlayerStatus {}
case object BuzzWinnerStatus extends ActivePlayerStatus {}
case object BuzzLoserStatus extends ActivePlayerStatus {}

case class ChooseQuestion (id: Long)

case class Buzz ()

case class ActivePlayerRecord (
    val id: Long, 
    val name: String, 
    var score: Int, 
    var status: ActivePlayerStatus, 
    val listener: ActorRef
) {
  def this (record: PlayerRecord) = this (
    record.id, 
    record.name, 
    0, 
    WaitingForChoiceStatus, 
    record.listener
   )
}

object JeopardyBoardHandler {
  def apply (system: ActorSystem, boardSelector: BoardSelectorService, multiplier: Int, 
      players: List[ActivePlayerRecord]): ActorRef = {
    system.actorOf (Props (classOf[JeopardyBoardHandler], boardSelector, multiplier, players))
  }
}

class JeopardyBoardHandler (boardSelector: BoardSelectorService, multiplier: Int, players: List[ActivePlayerRecord]) extends Actor {
  val board = boardSelector.makeBoard(multiplier)
  
  sendBoardStatus (board, players)
  
  override def receive = {
    case msg: ChooseQuestion => handleChooseQuestion (msg)
    case msg: Buzz => handleBuzz (msg)
    case msg => throw new UnsupportedOperationException (s"Unexpected message: ${msg}")
  }
  
  private def handleChooseQuestion (msg: ChooseQuestion) {
    val player = findPlayer (sender, msg)
    if (player.status != InControlStatus) {return}
    val questionColumns = board.columns.map {column => column.questions}
    val questions = questionColumns.flatten
    val questionOpt = questions.find {question => question.id == msg.id}
    questionOpt match {
      case Some (question) => {
        players.foreach {player =>
          player.status = WaitingForBuzzStatus
          player.listener ! AskQuestion (msg.id, question.text)
        }
      }
      case None => throw new IllegalArgumentException (s"Found questions with IDs ${questions.map {question => question.id}}, but no question with ID ${msg.id}")
    }
  }
  
  private def handleBuzz (msg: Buzz) {
    val winner = findPlayer (sender, msg)
    if (winner.status != WaitingForBuzzStatus) {return}
    players.foreach {player =>
      if (player.listener == sender) {
        player.status = BuzzWinnerStatus
        player.listener ! BuzzWinner ()
      }
      else {
        player.status = BuzzLoserStatus
        player.listener ! QuestionClaimed (winner.id)
      }
    }
  }
  
  private def sendBoardStatus (board: Board, players: List[ActivePlayerRecord]) {
    val columns = makeBoardColumns (board)
 		val msg = BoardStatus (players, columns)
    players.foreach {player =>
      player.listener ! msg
    }
  }
  
  private def makeBoardColumns (board: Board): List[services.routerplugins.BoardColumn] = {
    board.columns.map {inColumn =>
      val category = services.routerplugins.Category (inColumn.category.id, inColumn.category.name)
      val questions = inColumn.questions.map {inQuestion =>
        services.routerplugins.Question (
          inQuestion.id,
          inQuestion.value
        )
      }
      services.routerplugins.BoardColumn (category, questions)
    }
  }
  
  private def findPlayer (sender: ActorRef, msg: Any): ActivePlayerRecord = {
    players.find {_.listener == sender} match {
      case Some (player) => player
      case None => throw new IllegalArgumentException (s"Received message from unknown player: ${msg}")
    }
  }
}
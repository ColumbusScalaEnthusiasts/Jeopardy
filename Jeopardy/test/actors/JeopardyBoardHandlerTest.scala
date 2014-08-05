package actors

import org.junit.runner.RunWith
import org.scalatest.DoNotDiscover
import org.scalatest.path.FunSpec
import org.scalatest.junit.JUnitRunner
import services.routerplugins.PlayerInfo
import services.routerplugins.Ready
import services.routerplugins.PlayerList
import akka.testkit.TestActorRef
import services.routerplugins.SignedIn
import akka.actor.Props
import akka.actor.ActorSystem
import utils.TestUtils.Recorder
import org.mockito.Mockito._
import services.board.BoardSelectorService
import services.routerplugins.BoardStatus
import services.board.Board
import services.board.Answer
import services.board.Category
import services.board.BoardColumn
import services.board.Question
import akka.actor.ActorRef
import services.routerplugins.AskQuestion
import services.routerplugins.BuzzWinner
import services.routerplugins.QuestionClaimed

@DoNotDiscover
@RunWith(classOf[JUnitRunner])
class JeopardyBoardHandlerTest extends FunSpec {
  describe ("A JeopardyBoardHandler with a multiplier of 200 and players Jeffy and Chubs") {
    val system = ActorSystem ()
    val jeffy = ActivePlayerRecord (1234, "Jeffy", 0, InControlStatus, Recorder (system))
    val chubs = ActivePlayerRecord (2345, "Chubs", 0, WaitingForChoiceStatus, Recorder (system))
    val boardSelector = mock (classOf[BoardSelectorService])
    when (boardSelector.makeBoard (200)).thenReturn (makeTestBoard (200))
    val players = List (jeffy, chubs)
    val subject = TestActorRef[JeopardyBoardHandler] (
        Props (classOf[JeopardyBoardHandler], boardSelector, 200, players)
    ) (system)

    it ("sends a BoardStatus message to Jeffy saying that he's InControl") {
      val msg = findSentMessage[BoardStatus] (jeffy)
      assert (msg.playerStatus === InControlStatus)
      assert (msg.columns(0).category.name === "Category 1")
      assert (msg.columns(2).questions(3).value === 800)
    }
    
    it ("sends a BoardStatus message to Chubs saying that he's WaitingForChoice") {
      val msg = findSentMessage[BoardStatus] (chubs)
      assert (msg.playerStatus === WaitingForChoiceStatus)
    }
    
    describe ("when Jeffy chooses an available question") {
      clearRecorders (jeffy, chubs)
      receiveFrontEndMessage (jeffy, subject, ChooseQuestion (2, 3))
      
      it ("sends an AskQuestion message to Jeffy") {
        assert (findSentMessage[AskQuestion] (jeffy) === AskQuestion (2, 3, "Category 3, question 4"))
      }
      
      it ("sends an AskQuestion message to Chubs") {
        assert (findSentMessage[AskQuestion] (chubs) === AskQuestion (2, 3, "Category 3, question 4"))
      }
      
      describe ("and Chubs and Jeffy buzz in almost together") {
        clearRecorders (jeffy, chubs)
        receiveFrontEndMessage (chubs, subject, Buzz ())
        receiveFrontEndMessage (jeffy, subject, Buzz ())
        
        it ("sends QuestionClaimed to Jeffy") {
          assert (findSentMessage[QuestionClaimed] (jeffy) === QuestionClaimed (chubs.id))
        }
        
        it ("sends BuzzWinner to Chubs") {
          assert (findSentMessage[BuzzWinner] (chubs) === BuzzWinner ())
        }
      }
    }
    
    describe ("when Chubs tries to choose an available question") {
      clearRecorders (chubs)
      receiveFrontEndMessage (chubs, subject, ChooseQuestion (2, 3))
      
      it ("ignores him because he's not InControlStatus") {
        assertNothingSent (chubs)
      }
    }
    
    describe ("when Chubs tries to buzz") {
      clearRecorders (chubs)
      receiveFrontEndMessage (chubs, subject, Buzz ())
      
      it ("ignores him because he's not WaitingForBuzzStatus") {
        assertNothingSent (chubs)
      }
    }
    
    system.shutdown ()
  }
  
  private def assertNothingSent (player: ActivePlayerRecord) {
    val recorder = (player.listener).asInstanceOf[TestActorRef[Recorder]].underlyingActor
    assert (recorder.getRecording === Nil)
  }
  
  private def findSentMessage[T <: Any] (player: ActivePlayerRecord): T = {
    val recorder = (player.listener).asInstanceOf[TestActorRef[Recorder]].underlyingActor
    recorder.getRecording match {
      case Nil => throw new IllegalStateException ("Expecting a recorded message, found none")
      case _ :: Nil => // great
      case recording => throw new IllegalStateException (s"Expecting one recorded message, found ${recording}")
    }
    recorder.getRecording.head.asInstanceOf[T]
  }
  
  private def clearRecorders (players: ActivePlayerRecord*) {
    players.foreach {player =>
      val recorder = (player.listener).asInstanceOf[TestActorRef[Recorder]].underlyingActor
      recorder.erase()
    }
  }
  
  private def receiveFrontEndMessage (player: ActivePlayerRecord, subject: ActorRef, msg: Any) {
    val recorder = (player.listener).asInstanceOf[TestActorRef[Recorder]].underlyingActor
    recorder.relay  (subject, msg)
  }
  
  private def makeTestBoard (multiplier: Int): Board = {
    val wrongAnswer = Answer (1L, "Wrong Answer", false)
    val rightAnswer = Answer (2L, "Right Answer", true)
    val columns = List (1, 2, 3, 4, 5, 6).map {columnNumber =>
      val category = Category (100L + columnNumber, s"Category ${columnNumber}")
      val questions = List (1, 2, 3, 4, 5).map {rowNumber =>
        Question (
          (columnNumber * 100L) + rowNumber,
          s"Category ${columnNumber}, question ${rowNumber}",
          rowNumber * multiplier,
          List (wrongAnswer, rightAnswer)
        )
      }
      BoardColumn (category, questions)
    }
    return Board (columns)
  }
}

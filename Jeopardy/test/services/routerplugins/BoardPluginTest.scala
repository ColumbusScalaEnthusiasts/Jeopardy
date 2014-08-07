package services.routerplugins

import org.junit.runner.RunWith
import org.scalatest.DoNotDiscover
import org.scalatest.junit.JUnitRunner
import org.scalatest.path.FunSpec
import actors.Relay
import akka.actor.ActorSystem
import play.api.libs.json.Json
import utils.TestUtils.Recorder
import actors.WaitingForChoiceStatus
import actors.ActivePlayerRecord
import actors.ChooseQuestion
import actors.Buzz

@DoNotDiscover
@RunWith (classOf[JUnitRunner])
class BoardPluginTest extends FunSpec {
  describe ("A BoardPlugin, installed with mocks") {
    val system = ActorSystem ()
    val backEndHandler = Recorder (system)
    val router = Recorder (system)
    val outputSocket = Recorder (system)
    val subject = new BoardPlugin ()
    subject.install (backEndHandler, router, outputSocket)

    describe ("and sent a BoardStatus message from the back end") {
      val boardStatus = BoardStatus (
        (1 to 3).map {index =>
          ActivePlayerRecord (index, s"Player ${index}", index * 1000, WaitingForChoiceStatus, null)
        }.toList,
        (1 to 6).map {column =>
          BoardColumn (Category (100 + column, s"Category ${column}"), (1 to 5).map {row =>
            Question ((row * 6) + column, row * 200)
          }.toList)
        }.toList
      )
      router.underlyingActor.erase()
      subject.handleOutgoingMessage(boardStatus)
      
      it ("translates it correctly to JSON for the front end") {
        val msg = Json.parse ("""
{
	"type": "boardStatus",
  "data": {
    "players": [
			{"id": 1, "name": "Player 1", "score": 1000, "status": "WaitingForChoiceStatus"},
			{"id": 2, "name": "Player 2", "score": 2000, "status": "WaitingForChoiceStatus"},
			{"id": 3, "name": "Player 3", "score": 3000, "status": "WaitingForChoiceStatus"}
    ],
    "columns": [
			{"category": {"id": 101, "name": "Category 1"}, "questions": [
				{"id": 7, "value": 200},
				{"id": 13, "value": 400},
				{"id": 19, "value": 600},
				{"id": 25, "value": 800},
				{"id": 31, "value": 1000}
      ]},
			{"category": {"id": 102, "name": "Category 2"}, "questions": [
				{"id": 8, "value": 200},
				{"id": 14, "value": 400},
				{"id": 20, "value": 600},
				{"id": 26, "value": 800},
				{"id": 32, "value": 1000}
      ]},
			{"category": {"id": 103, "name": "Category 3"}, "questions": [
				{"id": 9, "value": 200},
				{"id": 15, "value": 400},
				{"id": 21, "value": 600},
				{"id": 27, "value": 800},
				{"id": 33, "value": 1000}
      ]},
			{"category": {"id": 104, "name": "Category 4"}, "questions": [
				{"id": 10, "value": 200},
				{"id": 16, "value": 400},
				{"id": 22, "value": 600},
				{"id": 28, "value": 800},
				{"id": 34, "value": 1000}
      ]},
			{"category": {"id": 105, "name": "Category 5"}, "questions": [
				{"id": 11, "value": 200},
				{"id": 17, "value": 400},
				{"id": 23, "value": 600},
				{"id": 29, "value": 800},
				{"id": 35, "value": 1000}
      ]},
			{"category": {"id": 106, "name": "Category 6"}, "questions": [
				{"id": 12, "value": 200},
				{"id": 18, "value": 400},
				{"id": 24, "value": 600},
				{"id": 30, "value": 800},
				{"id": 36, "value": 1000}
      ]}
		]
  }
}
""")
        assert (router.underlyingActor.getRecording === List(Relay (msg, outputSocket)))
      }
    }

    describe ("and sent an AskQuestion message from the back end") {
      val askQuestion = AskQuestion (3, 4, "How high is up?")
      router.underlyingActor.erase()
      subject.handleOutgoingMessage(askQuestion)
      
      it ("translates it correctly to JSON for the front end") {
        val msg = Json.parse ("""
{
	"type": "askQuestion",
  "data": {
    "categoryIndex": 3, "rowIndex": 4, "text": "How high is up?"
  }
}
""")
        assert (router.underlyingActor.getRecording === List(Relay (msg, outputSocket)))
      }
    }

    describe ("and sent a QuestionClaimed message from the back end") {
      val questionClaimed = QuestionClaimed (1234L)
      router.underlyingActor.erase()
      subject.handleOutgoingMessage(questionClaimed)
      
      it ("translates it correctly to JSON for the front end") {
        val msg = Json.parse ("""
{
	"type": "questionClaimed",
  "data": {
    "buzzWinnerId": 1234
  }
}
""")
        assert (router.underlyingActor.getRecording === List(Relay (msg, outputSocket)))
      }
    }

    describe ("and sent a BuzzWinner message from the back end") {
      val buzzWinner = BuzzWinner ()
      router.underlyingActor.erase()
      subject.handleOutgoingMessage(buzzWinner)
      
      it ("translates it correctly to JSON for the front end") {
        val msg = Json.parse ("""
{
	"type": "buzzWinner",
  "data": {}
}
""")
        assert (router.underlyingActor.getRecording === List(Relay (msg, outputSocket)))
      }
    }
    
    describe ("and sent a ChooseQuestion message from the front end") {
      val data = Json.parse ("""
{
	"categoryIndex": 4,
	"rowIndex": 3
}
""")
      router.underlyingActor.erase()
      subject.handleIncomingMessage("chooseQuestion", data)
      
      it ("translates it correctly from JSON for the back end") {
        val msg = ChooseQuestion (4, 3)
        assert (router.underlyingActor.getRecording === List (Relay (msg, backEndHandler)))
      }
    }
    
    describe ("and sent a Buzz message from the front end") {
      val data = Json.parse ("{}")
      router.underlyingActor.erase()
      subject.handleIncomingMessage("buzz", data)
      
      it ("translates it correctly from JSON for the back end") {
        val msg = Buzz ()
        assert (router.underlyingActor.getRecording === List (Relay (msg, backEndHandler)))
      }
    }
    
    system.shutdown ();
  }
}
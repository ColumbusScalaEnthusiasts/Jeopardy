package actors

import org.junit.runner.RunWith
import org.scalatest.DoNotDiscover
import org.scalatest.path.FunSpec
import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.TestActorRef
import play.api.libs.json.Json
import utils.TestUtils.Recorder
import org.scalatest.junit.JUnitRunner

@DoNotDiscover
@RunWith(classOf[JUnitRunner])
class VestibuleSocketHandlerTest extends FunSpec {
  describe ("A VestibuleSocketHandler") {
    val system = ActorSystem ()
    val out = Recorder (system)
    val vestibuleHandler = Recorder (system)
    val subject = TestActorRef (Props (classOf[VestibuleSocketHandler], vestibuleHandler, out))(system)
    
    it ("sends a NewConnection message to the VestibuleHandler on startup") {
      assert (vestibuleHandler.underlyingActor.getRecording === List (NewConnection ()))
    }
    
    describe ("when sent a SignIn message from the front end") {
      vestibuleHandler.underlyingActor.erase()
      val json = Json.parse ("""
{
  "type": "signIn",
  "data": {
    "name": "Billy Tarmash"
  }
}
""")
      subject ! json
      
      it ("relays the message to the VestibuleHandler") {
        assert (vestibuleHandler.underlyingActor.getRecording === List (SignIn ("Billy Tarmash")))
      }
    
      describe ("followed by a Ready message") {
        vestibuleHandler.underlyingActor.erase()
        val json = Json.parse ("""
{
  "type": "ready",
  "data": {}
}
""")
        subject ! json
        
        it ("relays the message to the VestibuleHandler") {
          assert (vestibuleHandler.underlyingActor.getRecording === List (ReadyMsg ()))
        }
      
        describe ("followed by a Start message") {
          vestibuleHandler.underlyingActor.erase()
          val json = Json.parse ("""
{
  "type": "start",
  "data": {}
}
""")
          subject ! json
          
          it ("relays the message to the VestibuleHandler") {
            assert (vestibuleHandler.underlyingActor.getRecording === List (StartMsg ()))
          }
        }
      }
    
      describe ("followed by a SignOut message") {
        vestibuleHandler.underlyingActor.erase()
        val json = Json.parse ("""
{
  "type": "signOut",
  "data": {}
}
""")
        subject ! json
        
        it ("relays the message to the VestibuleHandler") {
          assert (vestibuleHandler.underlyingActor.getRecording === List (SignOut ()))
        }
      }
    }
    
    describe ("when sent a SignedIn message from the VestibuleHandler") {
      subject ! SignedIn (42L)
      
      it ("relays the message to the front end") {
        assert (out.underlyingActor.getRecording === List (Json.parse ("""
{
  "type": "signedIn",
	"data": {
    "id": 42
  }
}
""")))
      }
    }
    
    describe ("when sent a PlayerList message from the VestibuleHandler") {
      subject ! PlayerList (List (
          PlayerInfo (123L, "Pookie", SignedIn), 
          PlayerInfo (321L, "Chomps", Ready)))
      
      it ("relays the message go the front end") {
        assert (out.underlyingActor.getRecording === List (Json.parse ("""
{
	"type": "playerList",
  "data": {
		"players": [
			{"name": "Pookie", "id": 123, "status": "signedIn"},
			{"name": "Chomps", "id": 321, "status": "ready"}
		]
	}
}
""")))
      }
    }
    
    system.shutdown()
  }
}
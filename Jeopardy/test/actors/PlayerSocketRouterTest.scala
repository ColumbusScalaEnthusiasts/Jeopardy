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
import services.routerplugins.RouterPlugin
import org.mockito.Mockito._
import services.routerplugins.PlayerInfo
import services.routerplugins.PlayerList
import services.routerplugins.SignedIn
import services.routerplugins.Ready

@DoNotDiscover
@RunWith(classOf[JUnitRunner])
class PlayerSocketRouterTest extends FunSpec {
  describe ("A PlayerSocketRouter") {
    val system = ActorSystem ()
    val outputSocket = Recorder (system)
    val vestibuleHandler = Recorder (system)
    val subject = TestActorRef (Props (classOf[PlayerSocketRouter], vestibuleHandler, outputSocket))(system)
    
    it ("sends a NewConnection message to the VestibuleHandler on startup") {
      assert (vestibuleHandler.underlyingActor.getRecording === List (NewConnection ()))
    }
    
    describe ("that receives a Relay message") {
      vestibuleHandler.underlyingActor.erase()
      subject ! Relay (ReadyMsg (), vestibuleHandler)
      
      it ("performs the relay") {
        assert (vestibuleHandler.underlyingActor.getRecording === List (ReadyMsg ()))
      }
    }
    
    describe ("when a mock plugin is installed") {
      val plugin = mock (classOf[RouterPlugin])
      subject ! InstallPlugin (plugin)
      
      it ("the plugin's onInstallation method is called") {
        verify (plugin).onInstallation()
      }
      
      describe ("and a different mock plugin is installed") {
        val differentPlugin = mock (classOf[RouterPlugin])
        subject ! InstallPlugin (differentPlugin)
        
        it ("the new plugin's onInstallation method is called") {
          verify (differentPlugin).onInstallation()
        }
        
        it ("the old plugin's onRemoval method is called") {
          verify (plugin).onRemoval()
        }
      }
      
      describe ("and a message from the front end is received") {
        outputSocket.underlyingActor.erase ()
        val json = Json.parse ("""
{
  "type": "glibbety",
  "data": {
    "wampum": "Floobie"
  }
}
""")
        subject ! json
        
        it ("the message is parsed and relayed to the plugin") {
          verify (plugin).handleIncomingMessage("glibbety", Json.parse ("""{"wampum": "Floobie"}"""))
        }
      }
      
      describe ("and a message from the back end is received") {
        vestibuleHandler.underlyingActor.erase()
        val msg = PlayerList (List (
            PlayerInfo (123L, "Pookie", SignedIn), 
            PlayerInfo (321L, "Chomps", Ready)
        ))
        subject ! msg
        
        it ("the message is relayed to the plugin") {
          verify (plugin).handleOutgoingMessage (msg)
        }
      }
    }
    
    system.shutdown()
  }
}
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
    val backEndHandler = Recorder (system)
    val subject = TestActorRef[PlayerSocketRouter](Props (classOf[PlayerSocketRouter], backEndHandler, outputSocket))(system)
    
    it ("sends a NewConnection message to the VestibuleHandler on startup") {
      assert (backEndHandler.underlyingActor.getRecording === List (NewConnection ()))
    }
    
    describe ("that receives a Relay message") {
      backEndHandler.underlyingActor.erase()
      subject ! Relay (ReadyMsg (), backEndHandler)
      
      it ("performs the relay") {
        assert (backEndHandler.underlyingActor.getRecording === List (ReadyMsg ()))
      }
    }
    
    describe ("when a mock plugin is installed without a back end") {
      val plugin = mock (classOf[RouterPlugin])
      subject ! InstallPluginAndBackEnd (plugin, None)
      
      it ("the plugin's onInstallation method is called") {
        verify (plugin).onInstallation()
      }
      
      it ("the back end handler remains the same") {
        assert (subject.underlyingActor.backEndHandler === backEndHandler)
      }
      
      describe ("and a different mock plugin and back end is installed") {
        val differentPlugin = mock (classOf[RouterPlugin])
        val differentBackEndHandler = Recorder (system)
        subject ! InstallPluginAndBackEnd (differentPlugin, Some (differentBackEndHandler))
        
        it ("the new plugin's onInstallation method is called") {
          verify (differentPlugin).onInstallation()
        }
        
        it ("the old plugin's onRemoval method is called") {
          verify (plugin).onRemoval()
        }
        
        it ("the back end handler is replaced") {
          assert (subject.underlyingActor.backEndHandler === differentBackEndHandler)
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
        backEndHandler.underlyingActor.erase()
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
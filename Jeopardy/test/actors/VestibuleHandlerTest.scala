package actors

import org.junit.runner.RunWith
import org.scalatest.DoNotDiscover
import org.scalatest.path.FunSpec
import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.TestActorRef
import utils.TestUtils.Recorder
import org.scalatest.junit.JUnitRunner
import services.routerplugins.PlayerList
import services.routerplugins.PlayerInfo
import services.routerplugins.SignedIn
import services.routerplugins.Ready
import akka.actor.ActorRef

@DoNotDiscover
@RunWith(classOf[JUnitRunner])
class VestibuleHandlerTest extends FunSpec {
  describe ("A VestibuleHandler") {
    val system = ActorSystem ()
    val subject = TestActorRef[VestibuleHandler] (Props (classOf[VestibuleHandler]))(system)
    
    describe ("when the first PlayerSocketRouter registers via NewConnection") {
      val tommy = Recorder (system)
      receiveFrontEndMessage (tommy, subject, NewConnection ())
      
      it ("generates an empty PlayerList message") {
        assertSentMessages (tommy, PlayerList (Nil))
      }
      
      describe ("and another PlayerSocketRouter registers via NewConnection") {
        clearRecorders (tommy)
        val ursula = Recorder (system)
        receiveFrontEndMessage (ursula, subject, NewConnection ())
        
        it ("generates empty PlayerList messages only to the newest socket handler") {
          assertSentMessages (tommy)
          assertSentMessages (ursula, PlayerList (Nil))
        }

        describe ("and the first socket handler signs in") {
          clearRecorders (tommy, ursula)
        	receiveFrontEndMessage (tommy, subject, SignIn ("Tommy"))
          
          it ("sends expected messages") {
            assertSentMessages (tommy, SignedIn (1), PlayerList (List (PlayerInfo (1, "Tommy", SignedIn))))
            assertSentMessages (ursula)
          }
          
          describe ("and the second socket handler signs in") {
            clearRecorders (tommy, ursula)
        	  receiveFrontEndMessage (ursula, subject, SignIn ("Ursula"))
            
            it ("sends expected messages") {
              val expectedPlayerList = PlayerList (List (
                  PlayerInfo (1, "Tommy", SignedIn), 
                  PlayerInfo (2, "Ursula", SignedIn)
              ))
              assertSentMessages (tommy, expectedPlayerList)
              assertSentMessages (ursula, SignedIn (2), expectedPlayerList)
            }
          
            describe ("and the first socket handler signals Ready") {
              clearRecorders (tommy, ursula)
            	receiveFrontEndMessage (tommy, subject, ReadyMsg ())
              
              it ("sends expected messages") {
                val expectedPlayerList = PlayerList (List (
                    PlayerInfo (1, "Tommy", Ready), 
                    PlayerInfo (2, "Ursula", SignedIn)
                ))
                assertSentMessages (tommy, expectedPlayerList)
                assertSentMessages (ursula, expectedPlayerList)
              }
            }
            
            describe ("and the first socket handler signs out") {
              clearRecorders (tommy, ursula)
              receiveFrontEndMessage (tommy, subject, SignOut ())
              
              it ("sends expected messages") {
                assertSentMessages (tommy)
                assertSentMessages (ursula, PlayerList (List (PlayerInfo (2, "Ursula", SignedIn))))
              }
            }
          }
        }
      }
    }
    
    system.shutdown ()
  }
  
  private def receiveFrontEndMessage (sender: TestActorRef[Recorder], recipient: ActorRef, msg: Any) {
    sender.underlyingActor.relay (recipient, msg)
  }
  
  private def clearRecorders (recorders: TestActorRef[Recorder]*) {
    recorders.foreach {_.underlyingActor.erase()}
  }
  
  private def assertSentMessages (recipient: TestActorRef[Recorder], msgs: Any*) {
    val recorder = recipient.underlyingActor
    assert (recorder.getRecording === msgs)
  }
}
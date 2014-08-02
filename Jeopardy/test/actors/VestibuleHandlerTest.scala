package actors

import org.junit.runner.RunWith
import org.scalatest.DoNotDiscover
import org.scalatest.path.FunSpec
import org.scalatest.junit.JUnitRunner
import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.actor.Props
import utils.TestUtils._

@DoNotDiscover
@RunWith(classOf[JUnitRunner])
class VestibuleHandlerTest extends FunSpec {
  describe ("A VestibuleHandler") {
    val system = ActorSystem ()
    val subject = TestActorRef[VestibuleHandler] (Props (classOf[VestibuleHandler]))(system)
    
    describe ("when the first VestibuleSocketHandler registers via NewConnection") {
      val socketHandler1 = Recorder (system)
      socketHandler1.underlyingActor.relay (subject, NewConnection ())
      
      it ("an empty PlayerList message is generated") {
        assert (socketHandler1.underlyingActor.getRecording === List (PlayerList (Nil)))
      }
      
      socketHandler1.underlyingActor.erase ()
      
      describe ("and another VestibuleSocketHandler registers via NewConnection") {
        val socketHandler2 = Recorder (system)
        socketHandler2.underlyingActor.relay (subject, NewConnection ())
        
        it ("empty PlayerList messages are generated only to the newest socket handler") {
          assert (socketHandler1.underlyingActor.getRecording === Nil)
          assert (socketHandler2.underlyingActor.getRecording === List (PlayerList (Nil)))
        }

        socketHandler1.underlyingActor.erase ()
        socketHandler2.underlyingActor.erase ()
        
        describe ("and the first socket handler signs in") {
          socketHandler1.underlyingActor.relay (subject, SignIn ("Tommy"))
          
          it ("handlers get expected messages") {
            assert (socketHandler1.underlyingActor.getRecording === List (
                SignedIn (1),
                PlayerList (List (PlayerInfo (1, "Tommy"))))
            )
            assert (socketHandler2.underlyingActor.getRecording === Nil)
          }
          
          socketHandler1.underlyingActor.erase ()
          socketHandler2.underlyingActor.erase ()
          
          describe ("and the second socket handler signs in") {
            socketHandler2.underlyingActor.relay (subject, SignIn ("Ursula"))
            
            it ("handlers get expected messages") {
              val expectedPlayerList = PlayerList (List (PlayerInfo (1, "Tommy"), PlayerInfo (2, "Ursula")))
              assert (socketHandler1.underlyingActor.getRecording === List (
                  expectedPlayerList
              ))
              assert (socketHandler2.underlyingActor.getRecording === List (
                  SignedIn (2),
                  expectedPlayerList
              ))
            }
          
            socketHandler1.underlyingActor.erase ()
            socketHandler2.underlyingActor.erase ()
            
            describe ("and the first socket handler signs out") {
              socketHandler1.underlyingActor.relay (subject, SignOut ());
              
              it ("handlers get expected messages") {
                assert (socketHandler1.underlyingActor.getRecording === Nil)
                assert (socketHandler2.underlyingActor.getRecording === List (
                    PlayerList (List (PlayerInfo (2, "Ursula")))
                ))
              }
            }
          }
        }
      }
    }
    
    system.shutdown ()
  }
}
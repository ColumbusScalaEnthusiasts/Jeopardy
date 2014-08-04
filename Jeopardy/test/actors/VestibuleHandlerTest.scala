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
                PlayerList (List (PlayerInfo (1, "Tommy", SignedIn))))
            )
            assert (socketHandler2.underlyingActor.getRecording === Nil)
          }
          
          socketHandler1.underlyingActor.erase ()
          socketHandler2.underlyingActor.erase ()
          
          describe ("and the second socket handler signs in") {
            socketHandler2.underlyingActor.relay (subject, SignIn ("Ursula"))
            
            it ("handlers get expected messages") {
              val expectedPlayerList = PlayerList (List (
                  PlayerInfo (1, "Tommy", SignedIn), 
                  PlayerInfo (2, "Ursula", SignedIn)
              ))
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
            
            describe ("and the first socket handler signals Ready") {
              socketHandler1.underlyingActor.relay (subject, ReadyMsg ())
              
              it ("handlers get expected messages") {
                val expectedPlayerList = PlayerList (List (
                    PlayerInfo (1, "Tommy", Ready), 
                    PlayerInfo (2, "Ursula", SignedIn)
                ))
                assert (socketHandler1.underlyingActor.getRecording === List (
                    expectedPlayerList
                ))
                assert (socketHandler2.underlyingActor.getRecording === List (
                    expectedPlayerList
                ))
              }
            }
            
            describe ("and the first socket handler signs out") {
              socketHandler1.underlyingActor.relay (subject, SignOut ());
              
              it ("handlers get expected messages") {
                assert (socketHandler1.underlyingActor.getRecording === Nil)
                assert (socketHandler2.underlyingActor.getRecording === List (
                    PlayerList (List (PlayerInfo (2, "Ursula", SignedIn)))
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
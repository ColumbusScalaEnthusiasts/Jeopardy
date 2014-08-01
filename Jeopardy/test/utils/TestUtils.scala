package utils

import play.libs.Akka
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.Props
import akka.testkit.TestActorRef
import scala.collection.mutable.ListBuffer

object TestUtils {
  object Recorder {
    def apply (implicit system: ActorSystem): TestActorRef[Recorder] = {
      return TestActorRef[Recorder]
    }
  }
  
  class Recorder extends Actor {
    private val recording = new ListBuffer[Any] ()
    
    def receive = {
      case msg => recording += msg
    }
    
    def relay (target: ActorRef, msg: Any) {
      target ! msg
    }
    
    def getRecording = recording.toList.reverse
    
    def erase () {
      recording.clear ()
    }
  }
}

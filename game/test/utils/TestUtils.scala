package utils

import org.apache.pekko.actor.Actor
import org.apache.pekko.actor.ActorRef
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.testkit.TestActorRef

import scala.collection.mutable.ListBuffer

object TestUtils {
  object Recorder {
    def apply(implicit system: ActorSystem): TestActorRef[Recorder] = {
      return TestActorRef[Recorder]
    }
  }

  class Recorder extends Actor {
    private val recording = new ListBuffer[Any]()

    def receive = { case msg =>
      recording += msg
    }

    def relay(target: ActorRef, msg: Any): Unit = {
      target ! msg
    }

    def getRecording = recording.toList

    def erase(): Unit = {
      recording.clear()
    }
  }
}

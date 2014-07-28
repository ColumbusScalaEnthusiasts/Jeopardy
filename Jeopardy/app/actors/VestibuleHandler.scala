package actors

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala

object VestibuleHandler {
  def apply (system: ActorSystem): ActorRef = {
    system.actorOf (Props (classOf[VestibuleHandler]))
  }
}

case class SignIn (name: String)
case class PlayerInfo (id: Long, name: String)
case class PlayerList (players: List[PlayerInfo])

case class PlayerRecord (
  val id: Long,
  val name: String,
  val listener: ActorRef
)

class VestibuleHandler extends Actor {
  private var nextId = 1;
  private var players = List[PlayerRecord]()
  
  override def receive = {
    case msg: SignIn => handleSignIn (msg)
  }
  
  private def handleSignIn (msg: SignIn) {
    players = PlayerRecord (nextId, msg.name, sender) :: players
    players.foreach {player =>
      player.listener ! PlayerList (players.map {p => PlayerInfo (p.id, p.name)})
    }
    sender ! SignedIn (nextId)
    nextId = nextId + 1
  }  
}
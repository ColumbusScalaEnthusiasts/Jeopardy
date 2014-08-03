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

case class NewConnection ()
case class SignIn (name: String)
case class ReadyMsg ()
case class StartMsg ()
case class SignOut ()

case class PlayerRecord (
  val id: Long,
  val name: String,
  val status: PlayerStatus,
  val listener: ActorRef
)

class VestibuleHandler extends Actor {
  private var nextId = 1;
  private var players = List[PlayerRecord]()
  
  override def receive = {
    case msg: NewConnection => handleNewConnection ()
    case msg: SignIn => handleSignIn (msg)
    case msg: ReadyMsg => handleReadyMsg ()
    case msg: SignOut => handleSignOut ()
  }
  
  private def handleNewConnection () {
    sender ! makePlayerList
  }
  
  private def handleSignIn (msg: SignIn) {
    players = PlayerRecord (nextId, msg.name, SignedIn, sender) :: players
    sendPlayerLists ()
    sender ! SignedIn (nextId)
    nextId = nextId + 1
  }
  
  private def handleReadyMsg () {
    players = players.map {player =>
      player.listener match {
        case listener if (listener == sender) => playerRecordWithStatus (player, Ready)
        case _ => player
      }
    }
    sendPlayerLists ()
  }
  
  private def handleSignOut () {
    players = players.filter {_.listener != sender}
    sendPlayerLists ()
  }
  
  private def sendPlayerLists () {
    players.foreach {player =>
      player.listener ! makePlayerList
    }
  }
  
  private def makePlayerList = PlayerList (players.map {p => PlayerInfo (p.id, p.name, p.status)}.reverse)
  
  private def playerRecordWithStatus (player: PlayerRecord, status: PlayerStatus): PlayerRecord = {
    PlayerRecord (
      player.id,
      player.name,
      status,
      player.listener
    )
  }
}
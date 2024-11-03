package actors

import org.apache.pekko.actor.*
import services.board.BoardSelectorService
import services.routerplugins.BoardPlugin
import services.routerplugins.GameStarting
import services.routerplugins.PlayerInfo
import services.routerplugins.PlayerList
import services.routerplugins.PlayerStatus
import services.routerplugins.Ready
import services.routerplugins.SignedIn

object VestibuleHandler {
  def apply(system: ActorSystem): ActorRef = {
    system.actorOf(Props(classOf[VestibuleHandler]))
  }
}

case class NewConnection()
case class SignIn(name: String)
case class ReadyMsg()
case class StartMsg()
case class SignOut()

case class PlayerRecord(
    val id: Long,
    val name: String,
    val status: PlayerStatus,
    val listener: ActorRef
)

class JeopardyBoardHandlerFactory {
  def make(
      system: ActorSystem,
      boardSelector: BoardSelectorService,
      multiplier: Int,
      players: List[ActivePlayerRecord]
  ): ActorRef = {
    JeopardyBoardHandler(system, boardSelector, multiplier, players)
  }
}

class VestibuleHandler extends Actor {
  var jeopardyBoardHandlerFactory = new JeopardyBoardHandlerFactory()
  private var nextId = 1
  private var players = List[PlayerRecord]()

  override def receive = {
    case msg: NewConnection => handleNewConnection()
    case msg: SignIn        => handleSignIn(msg)
    case msg: ReadyMsg      => handleReadyMsg()
    case msg: StartMsg      => handleStartMsg()
    case msg: SignOut       => handleSignOut()
  }

  private def handleNewConnection(): Unit = {
    sender() ! makePlayerList
  }

  private def handleSignIn(msg: SignIn): Unit = {
    players = PlayerRecord(nextId, msg.name, SignedIn, sender()) :: players
    sendPlayerLists()
    sender() ! SignedIn(nextId)
    nextId = nextId + 1
  }

  private def handleReadyMsg(): Unit = {
    players = players.map { player =>
      player.listener match {
        case listener if (listener == sender()) =>
          playerRecordWithStatus(player, Ready)
        case _ => player
      }
    }
    sendPlayerLists()
  }

  private def handleStartMsg(): Unit = {
    val readyPlayers = players.filter { _.status == Ready }
    if (readyPlayers.size < 2) { return }
    players = players.filter { player => !readyPlayers.contains(player) }
    handleStartMsgForUnreadyPlayers()
    handleStartMsgForReadyPlayers(readyPlayers)
  }

  private def handleStartMsgForUnreadyPlayers(): Unit = {
    sendPlayerLists()
  }

  private def handleStartMsgForReadyPlayers(
      players: List[PlayerRecord]
  ): Unit = {
    // TODO: The following line will need to move to bootstrap code when we get a database
    val boardSelector = new BoardSelectorService()

    val activePlayers = players.map { player =>
      ActivePlayerRecord(
        player.id,
        player.name,
        0,
        if (player.listener == sender()) { InControlStatus }
        else { WaitingForChoiceStatus },
        player.listener
      )
    }
    val boardHandler = jeopardyBoardHandlerFactory.make(
      context.system,
      boardSelector,
      200,
      activePlayers
    )
    players.foreach { player =>
      player.listener ! GameStarting()
      player.listener ! InstallPluginAndBackEnd(
        new BoardPlugin(),
        Some(boardHandler)
      )
    }
  }

  private def handleSignOut(): Unit = {
    players = players.filter { _.listener != sender() }
    sendPlayerLists()
  }

  private def sendPlayerLists(): Unit = {
    players.foreach { player =>
      player.listener ! makePlayerList
    }
  }

  private def makePlayerList = PlayerList(players.map { p =>
    PlayerInfo(p.id, p.name, p.status)
  }.reverse)

  private def playerRecordWithStatus(
      player: PlayerRecord,
      status: PlayerStatus
  ): PlayerRecord = {
    PlayerRecord(
      player.id,
      player.name,
      status,
      player.listener
    )
  }
}

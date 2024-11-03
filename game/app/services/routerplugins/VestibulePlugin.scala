package services.routerplugins

import play.api.libs.json.JsArray
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import utils.Utils.*

case class SignedIn(id: Long)
trait PlayerStatus { val json: String }
case object SignedIn extends PlayerStatus { override val json = "signedIn" }
case object Ready extends PlayerStatus { override val json = "ready" }
case class PlayerInfo(id: Long, name: String, status: PlayerStatus)
case class PlayerList(players: List[PlayerInfo])
case class GameStarting()

class VestibulePlugin extends RouterPlugin {

  override def onInstallation(): Unit = {
    send(actors.NewConnection(), backEndHandler)
  }

  override def handleIncomingMessage(
      msgType: String,
      msgData: JsValue
  ): Unit = {
    msgType match {
      case "signIn"  => handleSignIn((msgData \ "name").as[String])
      case "ready"   => handleReady()
      case "start"   => handleStart()
      case "signOut" => handleSignOut()
    }
  }

  override def handleOutgoingMessage(msg: Any): Unit = {
    msg match {
      case m: SignedIn     => handleSignedIn(m)
      case m: PlayerList   => handlePlayerList(m)
      case m: GameStarting => handleGameStarting()
    }
  }

  private def handleSignIn(name: String): Unit = {
    send(actors.SignIn(name), backEndHandler)
  }

  private def handleReady(): Unit = {
    send(actors.ReadyMsg(), backEndHandler)
  }

  private def handleStart(): Unit = {
    send(actors.StartMsg(), backEndHandler)
  }

  private def handleSignOut(): Unit = {
    send(actors.SignOut(), backEndHandler)
  }

  private def handleSignedIn(msg: SignedIn): Unit = {
    send(
      new JsObject(
        Map(
          ("type", new JsString("signedIn")),
          (
            "data",
            new JsObject(
              Map(
                ("id", new JsNumber(msg.id))
              )
            )
          )
        )
      ),
      outputSocket
    )
  }

  private def handlePlayerList(msg: PlayerList): Unit = {
    val players: IndexedSeq[JsValue] = msg.players.map { player =>
      new JsObject(
        Map(
          ("name", new JsString(player.name)),
          ("id", new JsNumber(player.id)),
          ("status", new JsString(player.status.json))
        )
      )
    }.toIndexedSeq

    send(
      new JsObject(
        Map(
          ("type", new JsString("playerList")),
          (
            "data",
            new JsObject(
              Map(
                ("players", new JsArray(players))
              )
            )
          )
        )
      ),
      outputSocket
    )
  }

  private def handleGameStarting(): Unit = {
    send(
      new JsObject(
        Map(
          ("type", new JsString("gameStarting")),
          ("data", new JsObject(Map()))
        )
      ),
      outputSocket
    )
  }
}

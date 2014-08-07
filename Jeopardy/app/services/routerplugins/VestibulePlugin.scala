package services.routerplugins

import play.api.libs.json.JsValue
import akka.actor.Actor
import akka.actor.ActorRef
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray
import play.api.libs.json.JsNumber
import play.api.libs.json.JsString

case class SignedIn (id: Long)
trait PlayerStatus {val json: String}
case object SignedIn extends PlayerStatus {override val json = "signedIn"}
case object Ready extends PlayerStatus {override val json = "ready"}
case class PlayerInfo (id: Long, name: String, status: PlayerStatus)
case class PlayerList (players: List[PlayerInfo])
case class GameStarting ()

class VestibulePlugin extends RouterPlugin {
  
  override def onInstallation () {
    send (actors.NewConnection (), backEndHandler)
  }
  
  override def handleIncomingMessage (msgType: String, msgData: JsValue) {
    msgType match {
      case "signIn" => handleSignIn (field (msgData, "name"))
      case "ready" => handleReady ()
      case "start" => handleStart ()
      case "signOut" => handleSignOut ()
    }
  }
  
  override def handleOutgoingMessage (msg: Any) {
    msg match {
      case m: SignedIn => handleSignedIn (m)
      case m: PlayerList => handlePlayerList (m)
      case m: GameStarting => handleGameStarting ()
    }
  }
  
  private def handleSignIn (name: String) {
    send (actors.SignIn (name), backEndHandler)
  }
  
  private def handleReady () {
    send (actors.ReadyMsg (), backEndHandler)
  }
  
  private def handleStart () {
    send (actors.StartMsg (), backEndHandler)
  }
  
  private def handleSignOut () {
    send (actors.SignOut (), backEndHandler)
  }
  
  private def handleSignedIn (msg: SignedIn) {
    send (new JsObject (List (
      ("type", new JsString ("signedIn")),
      ("data", new JsObject (List (
        ("id", new JsNumber (msg.id))
      )))
    )), outputSocket)
  }
  
  private def handlePlayerList (msg: PlayerList) {
    val players = msg.players.map {player =>
      new JsObject (List (
        ("name", new JsString (player.name)),
        ("id", new JsNumber (player.id)),
        ("status", new JsString (player.status.json))
      ))
    }
    send (new JsObject (List (
      ("type", new JsString ("playerList")),
      ("data", new JsObject (List (
        ("players", new JsArray (players))
      )))
    )), outputSocket)
  }
  
  private def handleGameStarting () {
    send (new JsObject (List (
      ("type", new JsString ("gameStarting")),
      ("data", new JsObject (Nil))
    )), outputSocket)
  }
  
  private def field (msg: JsValue, fieldName: String): String = {
    (msg \ fieldName).as[String]
  }
  
  private def numField (msg: JsValue, fieldName: String): Long = {
    (msg \ fieldName).as[Long]
  }
}

package actors

import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.Props
import play.api.libs.json.JsValue
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsNumber
import play.api.libs.json.JsArray

case class SignedIn (id: Long)
case class PlayerInfo (id: Long, name: String)
case class PlayerList (players: List[PlayerInfo])

class VestibuleSocketHandler (vestibuleHandler: ActorRef, out: ActorRef) extends Actor {
  
  vestibuleHandler ! NewConnection ()
  
  override def receive = {
    // from front end
    case msg: JsValue => handleJsValue (msg)
    
    // from back end
    case msg: SignedIn => handleSignedIn (msg)
    case msg: PlayerList => handlePlayerList (msg)
  }
  
  private def handleJsValue (msg: JsValue) {
    (msg \ "type").as[String] match {
      case "signIn" => handleSignIn (field (msg, "name"))
      case "signOut" => handleSignOut ()
    }
  }
  
  private def handleSignIn (name: String) {
    vestibuleHandler ! SignIn (name)
  }
  
  private def handleSignOut () {
    vestibuleHandler ! SignOut ()
  }
  
  private def handleSignedIn (msg: SignedIn) {
    out ! new JsObject (List (
      ("type", new JsString ("signedIn")),
      ("data", new JsObject (List (
        ("id", new JsNumber (msg.id))
      )))
    ))
  }
  
  private def handlePlayerList (msg: PlayerList) {
    val players = msg.players.map {player =>
      new JsObject (List (
        ("name", new JsString (player.name)),
        ("id", new JsNumber (player.id))
      ))
    }
    out ! new JsObject (List (
      ("type", new JsString ("playerList")),
      ("data", new JsObject (List (
        ("players", new JsArray (players))
      )))
    ))
  }
  
  private def field (msg: JsValue, fieldName: String): String = {
    ((msg \ "data") \ fieldName).as[String]
  }
  
  private def numField (msg: JsValue, fieldName: String): Long = {
    ((msg \ "data") \ fieldName).as[Long]
  }
}
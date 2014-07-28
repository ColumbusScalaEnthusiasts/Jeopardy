package actors

import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.Props
import play.api.libs.json.JsValue
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsNumber

case class SignedIn (id: Long)

class VestibuleSocketHandler (vestibuleHandler: ActorRef, out: ActorRef) extends Actor {
  override def receive = {
    case msg: JsValue => handleJsValue (msg)
    case msg: SignedIn => handleSignedIn (msg)
  }
  
  private def handleJsValue (msg: JsValue) {
    (msg \ "type").as[String] match {
      case "signIn" => handleSignIn (field (msg, "name"))
    }
  }
  
  private def handleSignedIn (msg: SignedIn) {
    sender ! new JsObject (List (
      ("type", new JsString ("signedIn")),
      ("data", new JsObject (List (
        ("id", new JsNumber (msg.id))
      )))
    ))
  }
  
  private def handleSignIn (name: String) {
    vestibuleHandler ! SignIn (name)
  }
  
  private def field (msg: JsValue, fieldName: String): String = {
    ((msg \ "data") \ fieldName).as[String]
  }
  
  private def numField (msg: JsValue, fieldName: String): Long = {
    ((msg \ "data") \ fieldName).as[Long]
  }
}
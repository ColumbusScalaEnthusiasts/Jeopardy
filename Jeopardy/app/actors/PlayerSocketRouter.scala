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
import services.routerplugins.RouterPlugin
import services.routerplugins.VestibulePlugin

case class InstallPlugin (plugin: RouterPlugin)
case class Relay (msg: Any, target: ActorRef)

class PlayerSocketRouter (backEndHandler: ActorRef, out: ActorRef) extends Actor {
  
  var plugin: RouterPlugin = null
  
  handleInstallPlugin (new VestibulePlugin ())
  
  override def receive = {
    case msg: Relay => msg.target ! msg.msg
    case msg: InstallPlugin => handleInstallPlugin (msg.plugin)
    case msg: JsValue => plugin.handleIncomingMessage((msg \ "type").as[String], (msg \ "data"))
    case msg => plugin.handleOutgoingMessage (msg)
  }
  
  private def handleInstallPlugin (plugin: RouterPlugin) {
    if (this.plugin != null) {this.plugin.onRemoval ()}
    this.plugin = plugin
    plugin.install (backEndHandler, self, out)
    plugin.onInstallation()
  }
}

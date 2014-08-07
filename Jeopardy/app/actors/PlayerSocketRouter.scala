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

case class InstallPluginAndBackEnd (plugin: RouterPlugin, backEndHandlerOpt: Option[ActorRef])
case class Relay (msg: Any, target: ActorRef)

class PlayerSocketRouter (var backEndHandler: ActorRef, outputSocket: ActorRef) extends Actor {
  
  var plugin: RouterPlugin = null
  
  handleInstallPlugin (new VestibulePlugin (), None)
  
  override def receive = {
    case msg: Relay => msg.target ! msg.msg
    case msg: InstallPluginAndBackEnd => handleInstallPlugin (msg.plugin, msg.backEndHandlerOpt)
    case msg: JsValue => plugin.handleIncomingMessage((msg \ "type").as[String], (msg \ "data"))
    case msg => plugin.handleOutgoingMessage (msg)
  }
  
  private def handleInstallPlugin (plugin: RouterPlugin, backEndHandlerOpt: Option[ActorRef]) {
    if (this.plugin != null) {this.plugin.onRemoval ()}
    this.plugin = plugin
    val newBackEndHandler = backEndHandlerOpt match {
      case Some (handler) => handler
      case None => backEndHandler
    }
    backEndHandler = newBackEndHandler
    plugin.install (newBackEndHandler, self, outputSocket)
    plugin.onInstallation()
  }
}

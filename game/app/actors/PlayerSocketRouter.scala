package actors

import org.apache.pekko
import play.api.Logging
import play.api.libs.json.JsValue
import services.routerplugins.RouterPlugin
import services.routerplugins.VestibulePlugin

import pekko.actor.{Actor, ActorRef}

case class InstallPluginAndBackEnd(
    plugin: RouterPlugin,
    backEndHandlerOpt: Option[ActorRef]
)

case class Relay(msg: Any, target: ActorRef)

class PlayerSocketRouter(var backEndHandler: ActorRef, outputSocket: ActorRef)
    extends Actor
    with Logging {

  var plugin: RouterPlugin = null

  handleInstallPlugin(new VestibulePlugin(), None)

  override def receive = {
    case msg: Relay => msg.target ! msg.msg
    case msg: InstallPluginAndBackEnd =>
      handleInstallPlugin(msg.plugin, msg.backEndHandlerOpt)
    case msg: JsValue =>
      handleIncomingMessage((msg \ "type").as[String], (msg \ "data").get)
    case msg => handleOutgoingMessage(msg)
  }

  private def handleIncomingMessage(msgType: String, msgData: JsValue): Unit = {
    logger.info(s"Received ${msgType}: ${msgData}")
    plugin.handleIncomingMessage(msgType, msgData)
  }

  private def handleOutgoingMessage(msg: Any): Unit = {
    plugin.handleOutgoingMessage(msg)
    logger.info(s"Sent ${msg}")
  }

  private def handleInstallPlugin(
      plugin: RouterPlugin,
      backEndHandlerOpt: Option[ActorRef]
  ): Unit = {
    if (this.plugin != null) { this.plugin.onRemoval() }
    this.plugin = plugin
    val newBackEndHandler = backEndHandlerOpt match {
      case Some(handler) => handler
      case None          => backEndHandler
    }
    backEndHandler = newBackEndHandler
    plugin.install(newBackEndHandler, self, outputSocket)
    plugin.onInstallation()
  }
}

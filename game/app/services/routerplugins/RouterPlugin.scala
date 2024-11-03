package services.routerplugins

import org.apache.pekko
import play.api.libs.json.JsValue

import pekko.actor.ActorRef

trait RouterPlugin {
  protected var backEndHandler: ActorRef = null
  protected var router: ActorRef = null
  protected var outputSocket: ActorRef = null

  def onInstallation(): Unit = {}
  def handleIncomingMessage(msgType: String, msgData: JsValue): Unit
  def handleOutgoingMessage(msg: Any): Unit
  def onRemoval(): Unit = {}

  def install(
      backEndHandler: ActorRef,
      router: ActorRef,
      outputSocket: ActorRef
  ): Unit = {
    this.backEndHandler = backEndHandler
    this.router = router
    this.outputSocket = outputSocket
  }

  protected def send(msg: Any, target: ActorRef): Unit = {
    router ! actors.Relay(msg, target)
  }
}

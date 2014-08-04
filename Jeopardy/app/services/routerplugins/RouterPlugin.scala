package services.routerplugins

import akka.actor.ActorRef
import play.api.libs.json.JsValue

trait RouterPlugin {
  protected var backEndHandler: ActorRef = null
  protected var router: ActorRef = null
  protected var outputSocket: ActorRef = null
  
  def onInstallation () {}
  def handleIncomingMessage (msgType: String, msgData: JsValue)
  def handleOutgoingMessage (msg: Any)
  def onRemoval () {}
  
  def install (backEndHandler: ActorRef, router: ActorRef, outputSocket: ActorRef) {
    this.backEndHandler = backEndHandler
    this.router = router;
    this.outputSocket = outputSocket
  }
  
  protected def send (msg: Any, target: ActorRef) {
    router ! actors.Relay (msg, target)
  }
}

package controllers

import scala.concurrent.duration._

import actors._
import akka.actor.ActorRef
import akka.actor.Props
import akka.util.Timeout
import json.JsonFormats._
import play.api.Play.current
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import play.api.mvc._
import play.libs.Akka
import utils.IpAddress

object Vestibule extends Controller {
  
  private var instance = new Vestibule ()

  val (dataEnumerator, dataChannel) = Concurrent.broadcast[JsValue]

  implicit val timeout = Timeout(2 second)
  
  def index = Action {instance.handleIndex ();}

  def mobileWebSocket = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    Props(new StreamMergingActor(dataChannel))
  }

  def wsinit = WebSocket.acceptWithActor[JsValue, JsValue] {request => out => instance.handleWsinit (out)}
}

class Vestibule extends Controller {
  private var vestibuleHandler = VestibuleHandler (Akka.system)
  
  def handleIndex () = {
    Ok(views.html.vestibule())
  }
  
  def handleWsinit (out: ActorRef) = {
    Props (new VestibuleSocketHandler (vestibuleHandler, out))
  }
}
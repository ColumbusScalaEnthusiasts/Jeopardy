package controllers.vestibule

import scala.concurrent.duration._
import actors._
import akka.actor.ActorRef
import akka.actor.Props
import play.api.Play.current
import play.api.libs.json.JsValue
import play.api.mvc._
import play.libs.Akka

trait Vestibule {
  this: Controller =>
    
  var vestibuleHandler = VestibuleHandler (Akka.system)
  
  def index = Action {
    Ok(views.html.vestibule())
  }

  // TODO: Dunno how to test this
  def wsinit = WebSocket.acceptWithActor[JsValue, JsValue] {request => out => 
    Props (new PlayerSocketRouter (vestibuleHandler, out))
  }
}

object Vestibule extends Controller with Vestibule

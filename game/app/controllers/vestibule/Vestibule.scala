package controllers.vestibule

import actors.*
import org.apache.pekko
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.*

import javax.inject.*

import pekko.actor.*

/*
trait Vestibule {
  this: Controller =>
  val system: ActorSystem

  var vestibuleHandler = VestibuleHandler { system }

  def index = Action {
    Ok(views.html.page())
  }

  // TODO: Dunno how to unit test this
  def wsinit = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    Props(new PlayerSocketRouter(vestibuleHandler, out))
  }
}
 */

class Vestibule @Inject() (
    val controllerComponents: ControllerComponents,
    implicit val actorSystem: ActorSystem
) extends BaseController {
  def index() = Action {
    Ok(views.html.page())
  }

  var vestibuleHandler = VestibuleHandler { actorSystem }

  // TODO: Dunno how to unit test this
  def wsinit() = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef { out =>
      Props(new PlayerSocketRouter(vestibuleHandler, out))
    }
  }
}

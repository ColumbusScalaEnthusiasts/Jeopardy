package controllers.vestibule

import org.scalatest.path.FunSpec
import play.libs.Akka
import utils.TestUtils.Recorder
import org.junit.runner.RunWith
import org.scalatest.DoNotDiscover
import org.scalatest.junit.JUnitRunner
import play.api.mvc.Controller
import play.api.test.FakeRequest
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.mvc.SimpleResult
import play.api.test.Helpers._
import play.api.test.WithApplication

@DoNotDiscover
@RunWith(classOf[JUnitRunner])
class VestibuleTest extends FunSpec {
	new WithApplication () {
    describe ("A Vestibule controller") {
      class SubjectController extends Controller with Vestibule
      
      val subject = new SubjectController ()
      
      describe ("when directed to handle an index request") {
        val result = subject.index ().apply (FakeRequest ())
        
        it ("produces an appropriate response") {
          assert (status (result) === 200)
          assert (contentAsString (result).contains ("Jeopardy.Vestibule.Controller.initialize"))
        }
      }
    }
  }
}
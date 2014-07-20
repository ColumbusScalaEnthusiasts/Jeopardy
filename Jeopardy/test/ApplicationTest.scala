import org.scalatest.DoNotDiscover
import play.api.test._
import play.api.test.Helpers._
import org.scalatest.path.FunSpec
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@DoNotDiscover
@RunWith(classOf[JUnitRunner])
class ApplicationTest extends FunSpec {

  describe ("An Application") {
    new WithApplication {
      describe ("when sent a bad request") {
        val result = route (FakeRequest (GET, "/boum"))
        
        it ("responds with a 404") {
          assert (result === None)
        }
      }
      
      describe ("when asked for the index page") {
        val result = route (FakeRequest (GET, "/")).get
        
        it ("returns the expected data") {
          assert (status (result) === OK)
          assert (contentType (result) === Some ("text/html"))
          assert (contentAsString (result).contains ("Device Orientation Demo"))
        }
      }
    }
  }
}

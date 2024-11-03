package controllers.vestibule

import org.apache.pekko
import org.junit.runner.RunWith
import org.scalatest.funspec
import org.scalatestplus.junit.JUnitRunner
import play.api.test.*
import play.api.test.Helpers.*

import pekko.actor.ActorSystem
import pekko.testkit.TestKit

@RunWith(classOf[JUnitRunner])
class VestibuleTest
    extends TestKit(ActorSystem("JeopardyBoardHandlerTest"))
    with funspec.PathAnyFunSpecLike {

  describe("A Vestibule controller") {
    val subject = new Vestibule(Helpers.stubControllerComponents(), system)

    describe("when directed to handle an index request") {
      val result = subject.index().apply(FakeRequest())

      it("produces an appropriate response") {
        assert(status(result) === 200)
      }

      it(
        "this seems pointless since functional tests show thing was run, this tests the actual text of integration"
      ) {
        contentAsString(result).contains(
          """<script src="/assets/javascripts/init.js" type="module"></script>"""
        )
      }
    }
  }

  TestKit.shutdownActorSystem(system)
}

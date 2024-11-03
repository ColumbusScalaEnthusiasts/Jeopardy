package services.routerplugins

import actors.NewConnection
import actors.ReadyMsg
import actors.Relay
import actors.SignIn
import actors.SignOut
import actors.StartMsg
import org.apache.pekko
import org.apache.pekko.testkit.TestKit
import org.junit.runner.RunWith
import org.scalatest.funspec
import org.scalatestplus.junit.JUnitRunner
import play.api.libs.json.Json
import utils.TestUtils.Recorder

import pekko.actor.ActorSystem

@RunWith(classOf[JUnitRunner])
class VestibulePluginTest
    extends TestKit(ActorSystem("JasmineTest"))
    with funspec.PathAnyFunSpecLike {

  describe("A VestibulePlugin, installed,") {
    val outputSocket = Recorder(system)
    val router = Recorder(system)
    val vestibuleHandler = Recorder(system)
    val subject = new VestibulePlugin()
    subject.install(vestibuleHandler, router, outputSocket)

    describe("when the onInstallation method is called") {
      subject.onInstallation()

      it("sends a NewConnection message to the VestibuleHandler") {
        assert(
          router.underlyingActor.getRecording === List(
            Relay(NewConnection(), vestibuleHandler)
          )
        )
      }
    }

    describe("when sent a SignIn message from the front end") {
      router.underlyingActor.erase()
      subject.handleIncomingMessage(
        "signIn",
        Json.parse("""{"name": "Billy Tarmash"}""")
      )

      it("relays the message to the VestibuleHandler") {
        assert(
          router.underlyingActor.getRecording === List(
            Relay(SignIn("Billy Tarmash"), vestibuleHandler)
          )
        )
      }

      describe("followed by a Ready message") {
        router.underlyingActor.erase()
        subject.handleIncomingMessage("ready", Json.parse("{}"))

        it("relays the message to the VestibuleHandler") {
          assert(
            router.underlyingActor.getRecording === List(
              Relay(ReadyMsg(), vestibuleHandler)
            )
          )
        }

        describe("followed by a Start message") {
          router.underlyingActor.erase()
          subject.handleIncomingMessage("start", Json.parse("{}"))

          it("relays the message to the VestibuleHandler") {
            assert(
              router.underlyingActor.getRecording === List(
                Relay(StartMsg(), vestibuleHandler)
              )
            )
          }
        }
      }

      describe("followed by a SignOut message") {
        router.underlyingActor.erase()
        subject.handleIncomingMessage("signOut", Json.parse("{}"))

        it("relays the message to the VestibuleHandler") {
          assert(
            router.underlyingActor.getRecording === List(
              Relay(SignOut(), vestibuleHandler)
            )
          )
        }
      }
    }

    describe("when sent a SignedIn message from the VestibuleHandler") {
      router.underlyingActor.erase()
      subject.handleOutgoingMessage(SignedIn(42L))

      it("relays the message to the front end") {
        assert(
          router.underlyingActor.getRecording === List(
            Relay(
              Json.parse("""
{
  "type": "signedIn",
  "data": {
    "id": 42
  }
}
"""),
              outputSocket
            )
          )
        )
      }
    }

    describe("when sent a PlayerList message from the VestibuleHandler") {
      router.underlyingActor.erase()
      subject.handleOutgoingMessage(
        PlayerList(
          List(
            PlayerInfo(123L, "Pookie", SignedIn),
            PlayerInfo(321L, "Chomps", Ready)
          )
        )
      )

      it("relays the message go the front end") {
        assert(
          router.underlyingActor.getRecording === List(
            Relay(
              Json.parse("""
{
  "type": "playerList",
  "data": {
    "players": [
      {"name": "Pookie", "id": 123, "status": "signedIn"},
      {"name": "Chomps", "id": 321, "status": "ready"}
    ]
  }
}
"""),
              outputSocket
            )
          )
        )
      }
    }

    describe("when sent a GameStarting message from the VestibuleHandler") {
      router.underlyingActor.erase()
      subject.handleOutgoingMessage(GameStarting())

      it("relays the message go the front end") {
        assert(
          router.underlyingActor.getRecording === List(
            Relay(
              Json.parse("""
{
  "type": "gameStarting",
  "data": {}
}
"""),
              outputSocket
            )
          )
        )
      }
    }
  }

  TestKit.shutdownActorSystem(system)
}

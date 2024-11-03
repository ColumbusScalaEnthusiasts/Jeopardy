package actors

import org.apache.pekko
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import org.scalatest.funspec
import org.scalatestplus.junit.JUnitRunner
import org.scalatestplus.mockito.MockitoSugar
import services.board.BoardSelectorService
import services.routerplugins.BoardPlugin
import services.routerplugins.GameStarting
import services.routerplugins.PlayerInfo
import services.routerplugins.PlayerList
import services.routerplugins.Ready
import services.routerplugins.SignedIn
import utils.TestUtils.Recorder

import pekko.actor.{ActorRef, ActorSystem}
import pekko.testkit.{TestActorRef, TestKit}
import pekko.testkit.TestKitBase

@RunWith(classOf[JUnitRunner])
class VestibuleHandlerTest
    extends TestKitBase
    with funspec.PathAnyFunSpecLike
    with MockitoSugar {

  implicit lazy val system: ActorSystem = ActorSystem(
    "VestibuleHandlerTest"
  )

  describe("A VestibuleHandler") {
    val subject = TestActorRef[VestibuleHandler]
    // TestActorRef[VestibuleHandler](Props(classOf[VestibuleHandler]))(system)

    describe("when Tommy registers via NewConnection") {
      val tommy = Recorder(system)
      receiveFrontEndMessage(tommy, subject, NewConnection())

      it("generates an empty PlayerList message to Tommy") {
        assertSentMessages(tommy, PlayerList(Nil))
      }

      describe("and Ursula registers via NewConnection") {
        clearRecorders(tommy)
        val ursula = Recorder(system)
        receiveFrontEndMessage(ursula, subject, NewConnection())

        it("generates empty PlayerList messages only to Ursula") {
          assertSentMessages(tommy)
          assertSentMessages(ursula, PlayerList(Nil))
        }

        describe("and Tommy signs in") {
          clearRecorders(tommy, ursula)
          receiveFrontEndMessage(tommy, subject, SignIn("Tommy"))

          it("sends player list to Tommy but not Ursula") {
            assertSentMessages(
              tommy,
              PlayerList(List(PlayerInfo(1, "Tommy", SignedIn))),
              SignedIn(1)
            )
            assertSentMessages(ursula)
          }

          describe("and Ursula signs in") {
            clearRecorders(tommy, ursula)
            receiveFrontEndMessage(ursula, subject, SignIn("Ursula"))

            it("sends player lists to Tommy and Ursula") {
              val expectedPlayerList = PlayerList(
                List(
                  PlayerInfo(1, "Tommy", SignedIn),
                  PlayerInfo(2, "Ursula", SignedIn)
                )
              )
              assertSentMessages(tommy, expectedPlayerList)
              assertSentMessages(ursula, expectedPlayerList, SignedIn(2))
            }

            describe("and Tommy signals Ready") {
              clearRecorders(tommy, ursula)
              receiveFrontEndMessage(tommy, subject, ReadyMsg())

              it("sends player lists to Tommy and Ursula") {
                val expectedPlayerList = PlayerList(
                  List(
                    PlayerInfo(1, "Tommy", Ready),
                    PlayerInfo(2, "Ursula", SignedIn)
                  )
                )
                assertSentMessages(tommy, expectedPlayerList)
                assertSentMessages(ursula, expectedPlayerList)
              }

              describe("and Tommy tries to start a game") {
                clearRecorders(tommy, ursula)
                receiveFrontEndMessage(tommy, subject, StartMsg())

                it("ignores him because he's the only one ready") {
                  assertSentMessages(tommy)
                  assertSentMessages(ursula)
                }
              }

              describe("and Valentina registers and signs in") {
                val valentina = Recorder(system)
                receiveFrontEndMessage(valentina, subject, NewConnection())
                receiveFrontEndMessage(valentina, subject, SignIn("Valentina"))

                describe("and Ursula signals Ready") {
                  receiveFrontEndMessage(ursula, subject, ReadyMsg())

                  describe("and Tommy starts a game") {
                    clearRecorders(tommy, ursula, valentina)
                    val activePlayers = List(
                      ActivePlayerRecord(
                        2,
                        "Ursula",
                        0,
                        WaitingForChoiceStatus,
                        ursula
                      ),
                      ActivePlayerRecord(1, "Tommy", 0, InControlStatus, tommy)
                    )
                    val boardHandler = mock[ActorRef]
                    val factory = mock[JeopardyBoardHandlerFactory]
                    when(
                      factory.make(
                        ArgumentMatchers.eq(system),
                        ArgumentMatchers.any(classOf[BoardSelectorService]),
                        ArgumentMatchers.eq(200),
                        ArgumentMatchers.eq(activePlayers)
                      )
                    ).thenReturn(boardHandler)
                    subject.underlyingActor.jeopardyBoardHandlerFactory =
                      factory
                    receiveFrontEndMessage(tommy, subject, StartMsg())

                    it("sends a slimmed-down player list to Valentina") {
                      assertSentMessages(
                        valentina,
                        PlayerList(List(PlayerInfo(3, "Valentina", SignedIn)))
                      )
                    }

                    val verifyMessages = { (player: TestActorRef[Recorder]) =>
                      val recording = player.underlyingActor.getRecording
                      assert(recording(0) === GameStarting())
                      val installationMsg =
                        recording(1).asInstanceOf[InstallPluginAndBackEnd]
                      assert(
                        installationMsg.plugin
                          .getClass() === classOf[BoardPlugin]
                      )
                      assert(
                        installationMsg.backEndHandlerOpt.get eq boardHandler
                      )
                    }

                    it(
                      "sends GameStarting and installation messages to Tommy and Ursula"
                    ) {
                      verifyMessages(tommy)
                      verifyMessages(ursula)
                    }
                  }
                }
              }
            }

            describe("and Tommy signs out") {
              clearRecorders(tommy, ursula)
              receiveFrontEndMessage(tommy, subject, SignOut())

              it("sends player list to Ursula but not Tommy") {
                assertSentMessages(tommy)
                assertSentMessages(
                  ursula,
                  PlayerList(List(PlayerInfo(2, "Ursula", SignedIn)))
                )
              }
            }
          }
        }
      }
    }
  }

  describe("A JeopardyBoardHandlerFactory") {
    val factory = new JeopardyBoardHandlerFactory()

    describe("when instructed to make a JeopardyBoardHandler") {
      val boardSelector = mock[BoardSelectorService]
      val result = factory.make(system, boardSelector, 0, Nil)

      it("does so, as far as anybody can tell") {
        assert(classOf[ActorRef].isAssignableFrom(result.getClass))
      }
    }
  }

  TestKit.shutdownActorSystem(system)

  private def receiveFrontEndMessage(
      sender: TestActorRef[Recorder],
      recipient: ActorRef,
      msg: Any
  ): Unit = {
    sender.underlyingActor.relay(recipient, msg)
  }

  private def clearRecorders(recorders: TestActorRef[Recorder]*): Unit = {
    recorders.foreach {
      _.underlyingActor.erase()
    }
  }

  private def assertSentMessages(
      recipient: TestActorRef[Recorder],
      msgs: Any*
  ): Unit = {
    val recorder = recipient.underlyingActor
    assert(recorder.getRecording === msgs)
  }
}

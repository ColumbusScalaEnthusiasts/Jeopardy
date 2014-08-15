package specs.vestibule

import org.junit.runner.RunWith
import org.scalatest.DoNotDiscover
import org.scalatest.FunSuite
import model.vestibule.Vestibule
import specs.JeopardyFunctional
import org.scalatest.junit.JUnitRunner
import pages.vestibule.Player
import model.board.Board

@RunWith (classOf[JUnitRunner])
class VestibuleBehavior extends FunSuite with JeopardyFunctional {
          
  test ("The Vestibule, already occupied, is entered by another player who then makes ready") {
    val billyContext = makeAnotherContext ()
    var billyVestibule: Vestibule = null
    val annieContext = makeAnotherContext ()
    var annieVestibule: Vestibule = null
    try {
      annieVestibule = new Vestibule ()(annieContext)
      annieVestibule.enterViaIndex()
      annieVestibule.signIn ("Annie")
      
      try {
        billyVestibule = new Vestibule ()(billyContext)
        billyVestibule.enterDirectly ()
        billyVestibule.signIn ("Billy")
        billyVestibule.ready ()
        
        try {
          var expectedPlayers = List (Player ("Annie", "signedIn"), Player ("Billy", "ready"))
          assert (billyVestibule.playersPresent == expectedPlayers)
          assert (annieVestibule.playersPresent == expectedPlayers)
          
          annieVestibule.ready ()
          
          expectedPlayers = List (Player ("Annie", "ready"), Player ("Billy", "ready"))
          assert (billyVestibule.playersPresent == expectedPlayers)
          assert (annieVestibule.playersPresent == expectedPlayers)
        }
        catch {
          case e: Exception => billyVestibule.signOut (); throw e
        }
      }
      catch {
        case e: Exception => annieVestibule.signOut (); throw e
      }
          
      annieVestibule.start ()
      
      val billyBoard = new Board ()(billyContext)
      assert (billyBoard.isDisplayed ())
      val annieBoard = new Board ()(annieContext)
      assert (annieBoard.isDisplayed ())
    }
    finally {
      annieContext.close ();
      billyContext.close ();
    }
    
    def playersAppear (expectedNames: List[String], vestibule: Vestibule) {
      val playersPresent = vestibule.playersPresent
      val actualNames = playersPresent.map {_.name}
      assert (actualNames === expectedNames)
    }
  }
}
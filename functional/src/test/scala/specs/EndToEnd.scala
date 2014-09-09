package specs

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import model.vestibule.Vestibule
import pages.vestibule.Player
import model.board.Board
import org.scalatest.junit.JUnitRunner
import model.database.DatabaseConditioner
import model.ScreenWithListedPlayers

@RunWith (classOf[JUnitRunner])
class EndToEnd extends FunSuite with JeopardyFunctional {
          
  test ("The Vestibule, already occupied, is entered by another player who then makes ready") {
    val databaseConditioner = new DatabaseConditioner ()
    databaseConditioner.conditionForTest ()
	  val annieContext = makeAnotherContext ()
    val billyContext = makeAnotherContext ()
    try {
      traverseVestibule (annieContext, billyContext)
      playARound (annieContext, billyContext)
    }
    finally {
      annieContext.close ();
      billyContext.close ();
      databaseConditioner.uncondition ()
    }
    
    def playersAppear (expectedNames: List[String], vestibule: Vestibule) {
      val playersPresent = vestibule.playersPresent
      val actualNames = playersPresent.map {_.name}
      assert (actualNames === expectedNames)
    }
  }
  
  private def traverseVestibule (annieContext: ContextPackage, billyContext: ContextPackage) {
  	val annieVestibule: Vestibule = new Vestibule ()(annieContext)
    annieVestibule.enterViaIndex()
    checkPlayers (List (annieVestibule), Nil)
    annieVestibule.signIn ("Annie")
    checkPlayers (List (annieVestibule), List (Player ("Annie", "signedIn")))
    
    try {
      val billyVestibule = new Vestibule ()(billyContext)
      billyVestibule.enterDirectly ()
      checkPlayers (List (annieVestibule), List (Player ("Annie", "signedIn")))
      checkPlayers (List (billyVestibule), List (Player ("Annie", "signedIn")))
      billyVestibule.signIn ("Billy")
      
      try {          
        checkPlayers (List (annieVestibule, billyVestibule), List (Player ("Annie", "signedIn"), Player ("Billy", "signedIn")))
        billyVestibule.ready ()
        checkPlayers (List (annieVestibule, billyVestibule), List (Player ("Annie", "signedIn"), Player ("Billy", "ready")))
        annieVestibule.ready ()
        checkPlayers (List (annieVestibule, billyVestibule), List (Player ("Annie", "ready"), Player ("Billy", "ready")))
        
        annieVestibule.start ()
      }
      catch {
        case e: Exception => billyVestibule.signOut (); throw e
      }
    }
    catch {
      case e: Exception => annieVestibule.signOut (); throw e
    }
  }
  
  def playARound (annieContext: ContextPackage, billyContext: ContextPackage) {
    val billyBoard = new Board ()(billyContext)
    assert (billyBoard.isDisplayed ())
    val annieBoard = new Board ()(annieContext)
    assert (annieBoard.isDisplayed ())
    // TODO: figure out how to import classes like BoardStatus from the main project into here,
    // then use them to condition the database and check here for categories and questions.
    assert (annieBoard.userInfo == Player ("Annie", 0, "InControlStatus"))
    assert (annieBoard.playersPresent == List (Player ("Billy", 0, "WaitingForChoiceStatus")))
    assert (billyBoard.userInfo == Player ("Billy", 0, "WaitingForChoiceStatus"))
    assert (billyBoard.playersPresent == List (Player ("Annie", 0, "InControlStatus")))
  }
  
  private def checkPlayers (screens: List[ScreenWithListedPlayers], players: List[Player]) {
    screens.foreach {screen => assert (screen.playersPresent == players)}
  }
}
package specs.vestibule

import specs.JeopardyFunctional
import org.scalatest.path.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import model.vestibule.Vestibule

@RunWith (classOf[JUnitRunner])
class VestibuleBehavior extends FeatureSpec with GivenWhenThen with JeopardyFunctional {
  functional {() =>
    feature ("The Vestibule") {
    
      scenario ("Player enters vestibule with nobody else around") {
        Given ("the Vestibule")
        val vestibule = new Vestibule
        
        When ("it is entered via the index page")
        vestibule.enterViaIndex ()

        Then ("there are no players present")
        assert (vestibule.playersPresent === Nil)
        
        When ("the player identifies himself")
        vestibule.signInPlayer ("Billy")
        
        Then ("he appears in the list")
        val playersPresent = vestibule.playersPresent
        val billy = playersPresent.head
        assert (billy.name === "Billy")
        assert (playersPresent.size === 1)
      }
      
      scenario ("Player enters vestibule second") {
        Given ("the Vestibule already entered by a player")
        val anotherContext = makeAnotherContext ()
        try {
          val anotherVestibule = new Vestibule ()(anotherContext)
          anotherVestibule.enterDirectly ()
          anotherVestibule.signInPlayer ("Annie")
          
          When ("our player enters later")
          val vestibule = new Vestibule
          vestibule.enterDirectly ()
          vestibule.signInPlayer ("Billy")
          
          Then ("both players appear in the list for both players")
          bothPlayersAppear (vestibule)
          bothPlayersAppear (anotherVestibule)
        }
        finally {
          anotherContext.close ();
        }
        
        def bothPlayersAppear (vestibule: Vestibule) {
          val playersPresent = vestibule.playersPresent
          val annie = playersPresent.head
          assert (annie.name === "Annie")
          val billy = playersPresent.tail.head
          assert (billy.name === "Billy")
          assert (playersPresent.size === 2)
        }
      }
    }
  }
}

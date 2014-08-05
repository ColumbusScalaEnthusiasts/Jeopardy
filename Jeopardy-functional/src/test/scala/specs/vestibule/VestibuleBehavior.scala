package specs.vestibule

import org.junit.runner.RunWith
import org.scalatest.DoNotDiscover
import org.scalatest.FunSuite
import model.vestibule.Vestibule
import specs.JeopardyFunctional
import org.scalatest.junit.JUnitRunner
import pages.vestibule.Player

@RunWith (classOf[JUnitRunner])
class VestibuleBehavior extends FunSuite with JeopardyFunctional {
    
      test ("Player enters vestibule with nobody else around") {
        val ctx = makeAnotherContext ()
        var vestibule: Vestibule = null
        try {
          vestibule = new Vestibule ()(ctx)
          vestibule.enterViaIndex ()
  
          assert (vestibule.playersPresent === Nil)
          
          vestibule.signIn ("Billy")
          
          try {
            val playersPresent = vestibule.playersPresent
            val billy = playersPresent.head
            assert (billy.name === "Billy")
            assert (playersPresent.size === 1)
          }
          finally {
            vestibule.signOut ()
          }
        }
        finally {
          ctx.close ()
        }
      }
      
      test ("The Vestibule, already occupied, is entered by another player who then makes ready") {
        val oneContext = makeAnotherContext ()
        var oneVestibule: Vestibule = null
        val anotherContext = makeAnotherContext ()
        var anotherVestibule: Vestibule = null
        try {
          anotherVestibule = new Vestibule ()(anotherContext)
          anotherVestibule.enterDirectly ()
          anotherVestibule.signIn ("Annie")
          
          try {
            oneVestibule = new Vestibule ()(oneContext)
            oneVestibule.enterDirectly ()
            oneVestibule.signIn ("Billy")
            oneVestibule.ready ("Billy")
            
            try {
              val expectedPlayers = List (Player ("Annie", "signedIn"), Player ("Billy", "ready"))
              assert (oneVestibule.playersPresent == expectedPlayers)
              assert (anotherVestibule.playersPresent == expectedPlayers)
            }
            finally {
              oneVestibule.signOut ()
            }
          }
          finally {
            anotherVestibule.signOut ()
          }
        }
        finally {
          anotherContext.close ();
          oneContext.close ();
        }
        
        def playersAppear (expectedNames: List[String], vestibule: Vestibule) {
          val playersPresent = vestibule.playersPresent
          val actualNames = playersPresent.map {_.name}
          assert (actualNames === expectedNames)
        }
    }
}
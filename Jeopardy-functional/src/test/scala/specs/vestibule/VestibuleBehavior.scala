package specs.vestibule

import org.junit.runner.RunWith
import org.scalatest.DoNotDiscover
import org.scalatest.FunSuite
import model.vestibule.Vestibule
import specs.JeopardyFunctional
import org.scalatest.junit.JUnitRunner

@DoNotDiscover
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
      
      test ("the Vestibule already entered by a player") {
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
            
            try {
              playersAppear (List ("Annie", "Billy"), oneVestibule)
              playersAppear (List ("Annie", "Billy"), anotherVestibule)
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
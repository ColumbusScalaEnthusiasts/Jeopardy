package specs.temporary

import org.scalatest.junit.JUnitRunner
import model.temporary.GoogleSearch
import org.scalatest.path.FunSpec
import org.junit.runner.RunWith
import specs.JeopardyFunctional

@RunWith (classOf[JUnitRunner])
class CSEIsListed extends FunSpec with JeopardyFunctional {
  functional {() =>
    describe ("Google") {
      val search = new GoogleSearch
  
      describe ("when searched for Columbus Scala Enthusiasts") {
        val results = search.searchFor ("Columbus Scala Enthusiasts")
        
        it ("shows a result for the Meetup group on the first page") {
          assert (results.map {_.linkName}.contains ("Columbus Scala Enthusiasts (Columbus, OH) - Meetup"))
        }
      }
    }
  }
}

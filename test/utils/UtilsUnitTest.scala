package utils

import actors.StreamMergingActor.OrientationChangeEvent
import actors.StreamMergingActor.OrientationChangeData
import org.junit.runner.RunWith
import org.scalatest.path.FunSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class UtilsUnitTest extends FunSpec {
  
  describe ("An IpAddress") {
    val ipAddress = new Object with IpAddress
    
    it ("contains localhost") {
      assert (ipAddress.getIpAddresses ().contains ("127.0.0.1"))
    }
  }
  
  describe ("A DegreesToRadians") {
    val degreesToRadians = new Object with DegreesToRadiansConversions
    
    describe ("given a degenerate orientation change") {
      val change = OrientationChangeEvent("test","test",5,OrientationChangeData(0.0,0.0,0.0))
      val result = degreesToRadians.convertDegreesToRadians(change)
      
      it ("returns another degenerate orientation change") {
        assert (result === change)
      }
    }
    
    describe ("given a meaty orientation change") {
      val change = OrientationChangeEvent("test","test",5,OrientationChangeData(90,180,45))
      val result = degreesToRadians.convertDegreesToRadians(change)
      
      it ("returns a similarly meaty orientation change using radians") {
        assert (result === OrientationChangeEvent("test", "test", 5, OrientationChangeData(
            Math.PI / 2.0,
            Math.PI,
            Math.PI / 4.0
        )))
      }
    }
  }
}

package frontend

import org.scalatest.path.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.selenium.WebBrowser
import org.scalatest.selenium.HtmlUnit
import java.io.File
import org.scalatest.selenium.Firefox

@RunWith (classOf[JUnitRunner])
class JasmineTest extends FunSpec with Firefox {
  describe ("When SpecRunner.html is loaded") {
    val fileUrl = makeFileUrl
    go to (fileUrl)
    val message = scrapeMessage
    val (specs, failures) = parseMessage (message)
    
    it ("the tests should exist and all pass") {
      assert (specs.toInt > 0)
      assert (failures.toInt === 0)
    }    
  }
  quit ()
  
  def makeFileUrl: String = {
    val currentDir = new File (".")
    return s"file:${currentDir.getAbsolutePath()}/test/javascripts/SpecRunner.html"
  }
  
  def scrapeMessage: String = {
    val elements = findAll (xpath ("/html/body/div/div/span[2]"))
    val barElement = elements.drop (1).next ();
    return barElement.text
  }
  
  def parseMessage (message: String): (String, String) = {
    val regex = """(\d+?) specs, (\d+?) failures""".r
    val regex (specs, failures) = message
    return (specs, failures)
  }
}

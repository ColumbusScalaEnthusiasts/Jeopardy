package frontend

import org.scalatest.DoNotDiscover
import org.scalatest.path.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.selenium.WebBrowser
import org.scalatest.selenium.HtmlUnit
import java.io.File
import org.scalatest.selenium.Firefox

@DoNotDiscover
@RunWith (classOf[JUnitRunner])
class JasmineTest extends FunSpec with HtmlUnit {

  private val scraper = new Jasmine131Scraper ();
  
  try {
    describe ("When SpecRunner.html is loaded") {
      val fileUrl = makeFileUrl
      go to (fileUrl)
      val (specs, failures) = waitForResult (10000L) {() =>
        scraper.scrapeResult()
      }

      it ("the tests should exist and all pass") {
        (specs, failures) match {
          case (0, _) => fail ("No Jasmine tests ran")
          case (_, 0) => // success!
          case (p, f) => fail (s"${f} of ${p} Jasmine test${if (p > 1) "s" else ""} failed")
        }
      }
    }
  }
  finally {
    quit ()
  }
  
  def waitForResult[T] (timeout: Long) (attempt: (() => T)): T = {
    val end = System.currentTimeMillis() + timeout;
    var lastException: Exception = null;
    while (System.currentTimeMillis () < end) {
      try {
        return attempt ()
      }
      catch {
        case e: Exception => {
          lastException = e
          Thread.sleep (100L)
        }
      }
    }
    throw lastException
  }

  def makeFileUrl: String = {
    val currentDir = new File (".")
    return s"file:${currentDir.getAbsolutePath()}/test/javascripts/SpecRunner.html"
  }
  
  trait JasmineScraper {
    def scrapeResult (): (Int, Int)
  
    protected def xpathQuery (query: String): List[String] = {
      val elements = findAll (xpath (query))
      elements.map {_.text}.toList
    }
  }
  
  class Jasmine131Scraper extends JasmineScraper {
    override def scrapeResult (): (Int, Int) = {
      val failureCountsOpt = findFailureCounts ()
      val successCountsOpt = findSuccessCounts ()
      (failureCountsOpt, successCountsOpt) match {
        case (None, Some (x)) => x
        case (Some (x), None) => x
        case _ => fail ("XPath search for summary message failed");
      }
    }
    
    private def findFailureCounts (): Option[(Int, Int)] = {
      val specsTexts = xpathQuery ("""//span[@class="resultsMenu bar"]/a[1]""")
      val failuresTexts = xpathQuery ("""//span[@class="resultsMenu bar"]/a[2]""")
      (specsTexts, failuresTexts) match {
        case (specs :: ss, failures :: fs) => Some ((parseFailSpecs (specs), parseFailFailures (failures)))
        case _ => None
      }
    }
    
    private def findSuccessCounts (): Option[(Int, Int)] = {
      val passingTexts = xpathQuery ("""//span[@class="passingAlert bar"]""")
      passingTexts match {
        case (passingText :: ps) => Some ((parsePassingSpecs (passingText), 0))
        case _ => None
      }
    }
    
    private def parseFailSpecs (specs: String): Int = {
      """(\d+) specs""".r.findFirstMatchIn (specs) match {
        case Some (m) => m.group(1).toInt
        case None => throw new IllegalStateException ("Expected \"<number> specs\"; got \"" + specs + "\"")
      }
    }
    
    private def parseFailFailures (failing: String): Int = {
      """(\d+) failing""".r.findFirstMatchIn (failing) match {
        case Some (m) => m.group(1).toInt
        case None => throw new IllegalStateException ("Expected \"<number> failing\"; got \"" + failing + "\"")
      }
    }
    
    private def parsePassingSpecs (specs: String): Int = {
      """Passing (\d+) specs""".r.findFirstMatchIn (specs) match {
        case Some (m) => m.group(1).toInt
        case None => throw new IllegalStateException ("Expected \"Passing <number> specs\"; got \"" + specs + "\"")
      }
    }
  }
  
  class Jasmine200Scraper extends JasmineScraper {
    override def scrapeResult (): (Int, Int) = {
      val message = scrapeMessage
      val (specs, failures) = parseMessage (message)
      (specs, failures)
    }
  
    def scrapeMessage: String = {
      val passTexts = xpathQuery ("""//span[@class="bar passed"]""")
      val failTexts = xpathQuery ("""//span[@class="bar failed"]""")
      (passTexts, failTexts) match {
        case (Nil, x :: _) => x
        case (x :: _, Nil) => x
        case _ => fail ("XPath search for summary message failed"); ""
      }
    }
    
    def parseMessage (message: String): (Int, Int) = {
      val regex = """(\d+?) specs, (\d+?) failures?""".r
      try {
        val regex (specs, failures) = message
        return (specs.toInt, failures.toInt)
      }
      catch {
        case e: Exception => fail (s"Message '${message}' didn't match specs/failures regex"); (0, 0)
      }
    }
  }
}
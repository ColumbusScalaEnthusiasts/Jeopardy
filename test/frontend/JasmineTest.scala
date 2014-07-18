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
  try {
    describe ("When SpecRunner.html is loaded") {
      val fileUrl = makeFileUrl
      go to (fileUrl)
      val (specs, failures) = waitForResult (10000L) {() =>
        val message = scrapeMessage
        val (specs, failures) = parseMessage (message)
        (specs, failures)
      }

      it ("the tests should exist and all pass") {
        (specs, failures) match {
          case (0, _) => fail ("No Jasmine tests ran")
          case (_, 0) => // success!
          case (_, f) => fail (s"${f} Jasmine test${if (f > 1) "s" else ""} failed")
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
  
  def xpathQuery (query: String): List[String] = {
    val elements = findAll (xpath (query))
    elements.map {_.text}.toList
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

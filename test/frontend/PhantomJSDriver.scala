package frontend

import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.scalatest.selenium.{Driver, WebBrowser}

trait GhostDriver extends WebBrowser with Driver {

    /**
     * The <code>FirefoxProfile</code> passed to the constructor of the <code>FirefoxDriver</code> returned by <code>webDriver</code>.
     *
     * <p>
     * The <code>FirefoxDriver</code> uses the <code>FirefoxProfile</code> defined as <code>firefoxProfile</code>. By default this is just a <code>new FirefoxProfile</code>.
     * You can mutate this object to modify the profile, or override <code>firefoxProfile</code>.
     * </p>
     */

    /**
     * <code>WebBrowser</code> subtrait that defines an implicit <code>WebDriver</code> for Firefox (an <code>org.openqa.selenium.firefox.FirefoxDriver</code>), with a default
     * Firefox profile.
     *
     * <p>
     * The <code>FirefoxDriver</code> uses the <code>FirefoxProfile</code> defined as <code>firefoxProfile</code>. By default this is just a <code>new FirefoxProfile</code>.
     * You can mutate this object to modify the profile, or override <code>firefoxProfile</code>.
     * </p>
     */
    implicit val webDriver: PhantomJSDriver = new PhantomJSDriver()

    /**
     * Captures a screenshot and saves it as a file in the specified directory.
     */
    def captureScreenshot(directory: String) {
      capture to directory
    }

}

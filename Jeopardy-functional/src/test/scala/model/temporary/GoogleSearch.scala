package model.temporary

import java.net.URI
import pages.Page
import pages.temporary.GooglePage
import org.openqa.selenium.WebDriver
import specs.ContextPackage

class GoogleSearch (implicit context: ContextPackage) {
  
  case class GoogleSearchResult (
    linkName: String,
    link: URI
  )

  def searchFor (searchString: String): List[GoogleSearchResult] = {
    val searchPage = Page (classOf[GooglePage])
    searchPage.goTo ()
    searchPage.setSearch (searchString)
    searchPage.doSearch ()
    searchPage.results.map {subpage =>
      new GoogleSearchResult (
        subpage.linkName,
        new URI (subpage.linkUrl)
      )
    }
  }
}
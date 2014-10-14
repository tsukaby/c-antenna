package controllers

import java.nio.file.{Files, Paths}

import com.tsukaby.c_antenna.Redis
import de.nava.informa.core.{ChannelIF, ItemIF}
import de.nava.informa.impl.basic.ChannelBuilder
import de.nava.informa.parsers.FeedParser
import org.openqa.selenium.{Dimension, OutputType}
import org.openqa.selenium.phantomjs.PhantomJSDriver
import play.api.mvc._
import scala.collection.JavaConverters._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def sample = Action {

    val rssUrl = "http://blog.livedoor.jp/itsoku/index.rdf"

    val channel: ChannelIF = Redis.get[ChannelIF](rssUrl) match {
      case Some(x) =>
        x
      case None =>
        val result = FeedParser.parse(new ChannelBuilder(), rssUrl)
        Redis.set(rssUrl, result, 60)
        result
    }

    println(channel.getTitle)
    println(channel.getSite.toString)

    channel.getItems.asScala foreach { case (x: ItemIF) =>
      println(x.getTitle)
      println(x.getLink)
    }
    Ok("")
  }

  def image(url: String) = Action {
    implicit val driver = new PhantomJSDriver()

    driver.get(url)
    driver.manage().window().setSize(new Dimension(1366, 768))
    val file = driver.getScreenshotAs(OutputType.FILE)
    Files.copy(Paths.get(file.toURI), Paths.get(file.getName))

    driver.quit()

    Ok("")
  }

}

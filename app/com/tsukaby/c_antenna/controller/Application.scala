package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.Redis
import com.tsukaby.c_antenna.service.SiteService
import de.nava.informa.core.{ChannelIF, ItemIF}
import de.nava.informa.impl.basic.ChannelBuilder
import de.nava.informa.parsers.FeedParser
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.{Dimension, OutputType}
import play.api.mvc.{Action, Controller}

import scala.collection.JavaConverters._

object Application extends Controller {

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


  private def getImage(url: String): Option[Array[Byte]] = {
    val driver: PhantomJSDriver = new PhantomJSDriver(new DesiredCapabilities())

    //driver.manage().window().setSize(new Dimension(1366, 768))
    driver.manage().window().setSize(new Dimension(400, 300))
    driver.get(url)
    val bytes: Array[Byte] = driver.getScreenshotAs(OutputType.BYTES)
    driver.quit()

    if (bytes == null || bytes.length == 0) {
      None
    } else {
      Option(bytes)
    }
  }

  def check = Action{

    SiteService.check

    Ok("done")
  }

}

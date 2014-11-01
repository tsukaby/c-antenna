package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.dao.RssDao
import de.nava.informa.core.ChannelIF
import org.openqa.selenium.phantomjs.{PhantomJSDriver, PhantomJSDriverService}
import org.openqa.selenium.remote.DesiredCapabilities

import scala.collection.JavaConverters._

/**
 * Webスクレイピング処理を行うクラスです。
 */
object WebScrapingService extends BaseService {

  private val driver: PhantomJSDriver = new PhantomJSDriver({
    val dcap = new DesiredCapabilities()
    // PhantomJSのログ出力を停止。細かいエラーが沢山出るため。
    dcap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, Array("--webdriver-loglevel=NONE"))
    dcap.setCapability("phantomjs.binary.path", "/usr/local/bin/phantomjs")
    dcap
  })

  /**
   * 引数で指定したページのテキストを取得します。
   * テキストはHTMLタグを除いたテキストです。
   *
   * @param articleUrl テキストを取得するページのURL
   * @param cssSelector 対象のページのテキストを取得する領域を示すCSSセレクタ
   * @return HTMLタグを除いたテキスト
   */
  def getText(articleUrl: String, cssSelector: String): Option[String] = synchronized {

    driver.get(articleUrl)

    val elements = driver.findElementsByCssSelector(cssSelector).asScala

    val str = if (elements.length == 0) {
      ""
    } else {
      elements map (x => x.getText) reduceLeft (_ + "\n" + _)
    }

    //driver.quit()

    if (str == null || str.isEmpty) {
      None
    } else {
      Option(str)
    }
  }

  def getTitle(url: String): String = {
    driver.get(url)

    val element = driver.findElementByTagName("title")

    element.getText
  }

  /**
   * informaライブラリを利用してRSSを取得します。
   * @param rssUrl RSSのURL
   * @return RSSオブジェクト
   */
  def getRss(rssUrl: String): Option[ChannelIF] = {
    RssDao.getByUrl(rssUrl)
  }


}

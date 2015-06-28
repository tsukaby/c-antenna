package com.tsukaby.c_antenna.service

import java.io.{ByteArrayOutputStream, FileInputStream}

import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.{Format, Image, Position}
import org.openqa.selenium.phantomjs.{PhantomJSDriver, PhantomJSDriverService}
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.{By, Dimension, OutputType}

import scala.collection.JavaConverters._

/**
 * Webスクレイピング処理を行うクラスです。
 */
trait WebScrapingService extends BaseService {

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

  /**
   * 引数で指定したWebページのサムネイル画像を取得します。
   * @param url サムネイル画像を取得するページのURL
   * @return サムネイル画像のバイナリ
   */
  def getImage(url: String): Array[Byte] = {
    implicit val driverTmp = new PhantomJSDriver()
    driverTmp.get(url)
    driverTmp.manage().window().setSize(new Dimension(1024, 768))
    val file = driverTmp.getScreenshotAs(OutputType.FILE)

    val fis = new FileInputStream(file)
    // 横幅を400に変更 GhostDriver側ではあくまでウィンドウのサイズを設定できるだけで、キャプチャは意図通りのサイズにならない
    // そのため、ここで400に変更
    // 縦を100にして保存
    //val img = Image(fis).scaleToWidth(400).resizeTo(400, 100, Position.TopLeft)
    implicit val writer = JpegWriter.apply(compression = 80, progressive = true)
    val img = Image(fis).scaleToWidth(400).resizeTo(400, 100, Position.TopLeft).stream

    fis.close()
    driverTmp.quit()

    val array = new Array[Byte](img.available())
    img.read(array)

    array
  }

  /**
   * 引数で指定したサイトのRSS URLを取得します。
   * @param url RSS URLを取得するサイトのトップページURL
   * @return RSS URL
   */
  def getRssUrl(url: String): Option[String] = {

    // URLに特定の文字列が入っている場合はRSS URLが分かる為、HTTPリクエスト無しで返す。
    // その他のURLの場合はHTMLをパースして返す

    if (url.contains("yahoo.co.jp")) {
      Some(url + "rss.xml")
    } else if (url.contains("seesaa.net")) {
      Some(url + "index20.rdf")
    } else if (url.contains("ameba.jp")) {
      Some(url + "rss.html")
    } else if (url.contains("fc2.")) {
      Some(url + "?xml")
    } else if (url.contains("blogspot.com")) {
      Some(url + "feeds/posts/default?alt=rss")
    } else if (url.contains("livedoor") || url.contains("ldblog") || url.contains("doorblog")) {
      Some(url + "index.rdf")
    } else {
      driver.get(url)

      // head内のlinkにapplication/rss+xmlがあれば、それがRSS URLなのでそれを返す
      driver.findElementByTagName("head").findElements(By.tagName("link")).asScala.find(_.getAttribute("type") == "application/rss+xml") match {
        case Some(x) => Some(x.getAttribute("href"))
        case None => None
      }
    }
  }
}

object WebScrapingService extends WebScrapingService

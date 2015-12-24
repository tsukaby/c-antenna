package com.tsukaby.c_antenna.service

import java.io.FileInputStream

import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.{Image, Position}
import org.openqa.selenium.phantomjs.{PhantomJSDriver, PhantomJSDriverService}
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.{Dimension, OutputType}

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
    val dcap = DesiredCapabilities.phantomjs()
    dcap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, Array("--webdriver-loglevel=NONE"))
    implicit val driverTmp = new PhantomJSDriver(dcap)
    driverTmp.get(url)
    driverTmp.manage().window().setSize(new Dimension(1024, 768))
    val file = driverTmp.getScreenshotAs(OutputType.FILE)

    val fis = new FileInputStream(file)
    // 横幅を400に変更 GhostDriver側ではあくまでウィンドウのサイズを設定できるだけで、キャプチャは意図通りのサイズにならない
    // そのため、ここで400に変更
    // 縦を100にして保存
    //val img = Image(fis).scaleToWidth(400).resizeTo(400, 100, Position.TopLeft)
    implicit val writer = JpegWriter.apply(compression = 80, progressive = true)
    val img = Image(fis).scaleToWidth(600).resizeTo(600, 150, Position.TopLeft).stream

    fis.close()
    driverTmp.quit()

    val array = new Array[Byte](img.available())
    img.read(array)

    array
  }
}

object WebScrapingService extends WebScrapingService

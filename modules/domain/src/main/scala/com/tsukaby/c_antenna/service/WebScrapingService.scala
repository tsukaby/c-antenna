package com.tsukaby.c_antenna.service

import java.io.ByteArrayInputStream

import com.sksamuel.scrimage.{Position, Image}
import com.sksamuel.scrimage.nio.JpegWriter
import org.openqa.selenium.phantomjs.{PhantomJSDriver, PhantomJSDriverService}
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.{Dimension, OutputType}

/**
 * Webスクレイピング処理を行うクラスです。
 */
trait WebScrapingService extends BaseService {
  
  val driver: PhantomJSDriver = {
    Logger.info("Initialize phantomjs driver.")
    val dcap = DesiredCapabilities.phantomjs()
    dcap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, Array("--webdriver-loglevel=NONE"))
    dcap.setCapability("phantomjs.binary.path", "/usr/local/bin/phantomjs")
    Logger.info("Create PhantomJsDriver.")
    new PhantomJSDriver(dcap)
  }

  /**
   * 引数で指定したWebページのサムネイル画像を取得します。
   * @param url サムネイル画像を取得するページのURL
   * @return サムネイル画像のバイナリ
   */
  def getImage(url: String): Array[Byte] = {
    Logger.info("PhantomJsDriver setup.")
    driver.get(url)
    driver.manage().window().setSize(new Dimension(1024, 768))
    Logger.info("PhantomJsDriver take a screenshot.")
    val bytes = driver.getScreenshotAs(OutputType.BYTES)
    Logger.info("Quit driver.")
    driver.quit()

    Logger.info("Create stream.")
    val in = new ByteArrayInputStream(bytes)
    // 横幅を変更 GhostDriver側ではあくまでウィンドウのサイズを設定できるだけで、キャプチャは意図通りのサイズにならない
    Logger.info("Compression and resize.")
    implicit val writer = JpegWriter.apply(compression = 80, progressive = true)
    val array = Image.fromStream(in).scaleToWidth(600).resizeTo(600, 150, Position.TopLeft).bytes
    Logger.info("Close stream.")
    in.close()
    Logger.info("Complete.")

    array
  }
}

object WebScrapingService extends WebScrapingService

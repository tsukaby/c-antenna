package com.tsukaby.c_antenna.service

import org.openqa.selenium.phantomjs.{PhantomJSDriverService, PhantomJSDriver}
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
}

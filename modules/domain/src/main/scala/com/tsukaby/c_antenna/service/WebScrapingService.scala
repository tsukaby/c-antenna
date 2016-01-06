package com.tsukaby.c_antenna.service

import java.io.ByteArrayInputStream

import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.{Image, Position}

import scala.util.Try

/**
  * Webスクレイピング処理を行うクラスです。
  */
trait WebScrapingService extends BaseService {

  /**
    * 引数で指定したWebページのサムネイル画像を取得します。
    * @param url サムネイル画像を取得するページのURL
    * @return サムネイル画像のバイナリ
    */
  def getImage(url: String): Array[Byte] = {
    Logger.info("PhantomJSDriverAlt take a screenshot.")
    val bytesTry: Try[Array[Byte]] = PhantomJSDriverAlt.getScreenshot(url, "1024px*768px", ByteArray)
    bytesTry.map { bytes =>
      Logger.info("Create stream.")
      val in = new ByteArrayInputStream(bytes)
      Logger.info("Compression and resize.")
      implicit val writer = JpegWriter.apply(compression = 80, progressive = true)
      val array = Image.fromStream(in).scaleToWidth(600).resizeTo(600, 150, Position.TopLeft).bytes
      Logger.info("Close stream.")
      in.close()
      Logger.info("Complete.")

      array
    } getOrElse Array()
  }
}

object WebScrapingService extends WebScrapingService

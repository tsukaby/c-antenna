package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.dao.SiteDao

/**
 * サムネイルに関する処理を行います。
 */
trait ThumbnailService {

  val siteDao:SiteDao = SiteDao

  /**
   * 引数で指定したサイトのサムネイルを取得します。
   * @param id サイトのID
   * @return サイトのサムネイル画像バイナリ
   */
  def getSiteThumbnail(id: Long): Option[Array[Byte]] = {
    siteDao.getById(id) match {
      case Some(x) => x.thumbnail
      case None => None
    }
  }
}

object ThumbnailService extends ThumbnailService

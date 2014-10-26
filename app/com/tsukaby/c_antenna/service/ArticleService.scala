package com.tsukaby.c_antenna.service

import com.tsukaby.c_antenna.dao.ArticleDao
import com.tsukaby.c_antenna.entity.Article
import com.tsukaby.c_antenna.entity.ImplicitConverter._

/**
 * 記事に関する処理を行うクラスです。
 */
object ArticleService {
  def getLately: Seq[Article] = {

    ArticleDao.getLately

  }
}

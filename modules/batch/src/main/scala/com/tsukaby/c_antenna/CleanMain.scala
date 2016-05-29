package com.tsukaby.c_antenna

import com.tsukaby.c_antenna.service.ArticleService
import kamon.Kamon
import scalikejdbc.config.DBs

import scala.language.postfixOps

object CleanMain extends BatchSupport {
  override def run(args:Array[String]): Unit = {
    args.toList match {
      case "delete_article" :: Nil =>
        ArticleService.deleteOldArticles()
      case x =>
        throw new IllegalArgumentException(s"args = $x")
    }
  }

  def main(args: Array[String]): Unit = {
    Kamon.start()
    DBs.setupAll()

    try {
      run(args)
    } finally {
      Kamon.shutdown()
    }
  }
}

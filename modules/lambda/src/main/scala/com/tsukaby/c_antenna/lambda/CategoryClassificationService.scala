package com.tsukaby.c_antenna.lambda

import com.tsukaby.bayes.classifier.{BayesClassifier, Classification}

/**
 * カテゴリ判別を行うサービスです。
 */
trait CategoryClassificationService {

  val bayes = new BayesClassifier[String, String]()

  // Learning
  learn()

  private def learn() =  {
    bayes.learn("technology", "github" :: "git" :: "tech" :: "technology" :: Nil)
    bayes.learn("weather", "sun" :: "rain" :: "cloud" :: "weather" :: "snow" :: Nil)
    bayes.learn("government", "ballot" :: "winner" :: "party" :: "money" :: "candidate" :: Nil)
  }


  /**
   * 引数で与えた形態素解析済みのテキストから該当するカテゴリを取得します。
   * @param words 単語単位に形態素解析されている解析対象テキスト
   * @return 判別されたカテゴリ
   */
  def classify(words: Traversable[String]): String = {
    val result: Option[Classification[String, String]] = bayes.classify(words)
    result.map(_.category).getOrElse("Unknown")
  }
}

object CategoryClassificationService extends CategoryClassificationService

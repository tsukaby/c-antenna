package com.tsukaby.c_antenna.lambda

import java.io.{BufferedReader, InputStreamReader}

import com.github.tototoshi.csv.CSVReader
import com.tsukaby.bayes.classifier.{BayesClassifier, Classification}

/**
 * カテゴリ判別を行うサービスです。
 */
trait CategoryClassificationService {

  val is = this.getClass.getClassLoader.getResourceAsStream("categories_training_data.csv")
  val br = new BufferedReader(new InputStreamReader(is, "UTF-8"))
  val reader = CSVReader.open(br)

  val bayes = new BayesClassifier[String, String]()

  // Learning
  learn()

  private def learn() =  {
    reader.all().map { line =>
      (line.head, line(1))
    } groupBy (_._1) foreach { case (category, learningData) =>
      bayes.learn(category, learningData.map(_._2))
    }
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

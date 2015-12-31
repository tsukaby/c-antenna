package com.tsukaby.c_antenna.lambda

import java.io.{BufferedReader, InputStream, InputStreamReader, OutputStream}

import com.github.tototoshi.csv.CSVReader
import com.tsukaby.bayes.classifier.{BayesClassifier, Classification}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization

import scala.io.{Codec, Source}

case class ClassificationRequest(words: Seq[String])

case class ClassificationResponse(category: String)

class CategoryClassification {
  implicit val formats = DefaultFormats

  val is = this.getClass.getClassLoader.getResourceAsStream("categories_training_data.csv")
  val br = new BufferedReader(new InputStreamReader(is, "UTF-8"))
  val reader = CSVReader.open(br)

  val bayes = new BayesClassifier[String, String]()

  // Learning
  learn()

  def classify(input: InputStream, output: OutputStream): Unit = {
    val req = parse(Source.fromInputStream(input)(Codec.UTF8).mkString).extract[ClassificationRequest]
    val response = ClassificationResponse(classifyBy(req.words))
    val responseStr = Serialization.write(response)

    val result = responseStr.getBytes("UTF-8")
    output.write(result)
  }

  private def learn() = {
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
  private def classifyBy(words: Traversable[String]): String = {
    val result: Option[Classification[String, String]] = bayes.classify(words)
    result.map(_.category).getOrElse("Unknown")
  }
}

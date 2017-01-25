package com.tsukaby.c_antenna.lambda

import java.io.{InputStream, OutputStream}

import com.atilika.kuromoji.ipadic.{Token, Tokenizer}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization

import scala.collection.JavaConverters._
import scala.io.{Codec, Source}

case class AnalyzeRequest(text: String)

case class AnalyzeResponse(tags: Seq[String])

class Morphological {
  implicit val formats = DefaultFormats

  private val tokenizer: Tokenizer = {
    val input = this.getClass.getClassLoader.getResourceAsStream("my-userdict.txt")
    val tokenizer = new Tokenizer.Builder().userDictionary(input).build()
    input.close()
    tokenizer
  }

  def analyze(input: InputStream, output: OutputStream): Unit = {
    val textInfo = parse(Source.fromInputStream(input)(Codec.UTF8).mkString).extract[AnalyzeRequest]
    val response = AnalyzeResponse(getTags(textInfo.text).map(_._1))
    val responseStr = Serialization.write(response)

    val result = responseStr.getBytes("UTF-8")
    output.write(result)
  }

  /**
    * 引数で指定したテキストからタグを生成し、取得します。
    * @param text タグにするテキスト
    * @return 頻度順にソートされたタグのリスト
    */
  def getTags(text: String): Seq[(String, Int)] = {

    val tokensNormal: List[Token] = tokenizer.tokenize(text).asScala.toList

    // 必要な単語だけに絞る
    val filteredTokens = tokensNormal filter { token =>
      val first = token.getAllFeaturesArray.array(0)
      // 品詞2つ目は存在しない場合があるため、制御
      val second = if (token.getAllFeaturesArray.length < 2) "" else token.getAllFeaturesArray.array(1)
      // 辞書登録されている用語または、未知語でなく名詞だけに絞る
      token.isUser || (first == "名詞" && second != "サ変接続" && second != "数" && second != "接尾" && second != "代名詞" && second != "非自立")
    } filterNot { token =>
      // アルファベット１文字は弾く
      token.getSurface.matches( """[a-zA-Z]""")
    }

    // 名称だけのリストに変換
    val words = filteredTokens map (x => x.getSurface)

    // 出現頻度のMapに変換
    words.groupBy(identity).mapValues(_.size).toSeq.sortWith(_._2 > _._2)

  }
}

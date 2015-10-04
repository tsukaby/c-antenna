package com.tsukaby.c_antenna.lambda

import org.atilika.kuromoji.{Token, Tokenizer}

import scala.collection.JavaConverters._

/**
 * 形態素解析処理を行うサービスです。
 */
trait MorphologicalService {

  private val tokenizer: Tokenizer = {
    val input = this.getClass.getClassLoader.getResourceAsStream("my-userdict.txt")
    val tokenizer = Tokenizer.builder().userDictionary(input).build()
    input.close()
    tokenizer
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
    }

    // 名称だけのリストに変換
    val words = filteredTokens map (x => x.getSurfaceForm)

    // 出現頻度のMapに変換
    words.groupBy(identity).mapValues(_.size).toSeq.sortWith(_._2 > _._2)

  }
}

object MorphologicalService extends MorphologicalService

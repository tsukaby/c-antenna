package com.tsukaby.c_antenna.lambda

import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.AWSLambdaClient
import com.amazonaws.services.lambda.invoke.{LambdaFunction, LambdaInvokerFactory}

class AnalyzeRequest(var text: String) {
  def getText: String = text

  def setText(text: String): Unit = this.text = text
}

class AnalyzeResponse() {
  var tags: Array[String] = Array()

  def getTags: Array[String] = tags

  def setTags(tags: Array[String]): Unit = this.tags = tags
}

class ClassificationRequest(var words: Array[String] = Array()) {
  def getWords: Array[String] = words

  def setWords(words: Array[String]): Unit = this.words = words
}

class ClassificationResponse() {
  var category: String = ""

  def getCategory: String = category

  def setCategory(category: String): Unit = this.category = category
}

class RssUrlFindRequest(var pageUrl: String) {
  def getPageUrl: String = pageUrl

  def setPageUrl(pageUrl: String): Unit = this.pageUrl = pageUrl
}

class RssUrlFindResponse() {
  var rssUrl: String = ""

  def getRssUrl: String = rssUrl

  def setRssUrl(rssUrl: String): Unit = this.rssUrl = rssUrl
}

trait LambdaInvoker {
  @LambdaFunction(functionName = "Morphological_analyze")
  def analyzeMorphological(input: AnalyzeRequest): AnalyzeResponse

  @LambdaFunction(functionName = "CategoryClassification_classify")
  def classifyCategory(input: ClassificationRequest): ClassificationResponse

  @LambdaFunction(functionName = "Rss_findRssUrl")
  def findRssUrl(input: RssUrlFindRequest): RssUrlFindResponse
}

object LambdaInvoker {
  // Set environment variables, AWS_ACCESS_KEY, AWS_SECRET_KEY
  lazy val lambda: AWSLambdaClient = {
    val tmp = new AWSLambdaClient()
    tmp.configureRegion(Regions.AP_NORTHEAST_1)
    tmp
  }

  lazy val morphological = LambdaInvokerFactory.build(classOf[LambdaInvoker], lambda)

  def apply(): LambdaInvoker = {
    morphological
  }
}

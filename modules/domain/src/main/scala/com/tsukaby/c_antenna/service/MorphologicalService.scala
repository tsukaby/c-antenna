package com.tsukaby.c_antenna.service

import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.AWSLambdaClient
import com.amazonaws.services.lambda.invoke.{LambdaFunction, LambdaInvokerFactory}
import com.tsukaby.c_antenna.entity.{AnalyzeRequest, AnalyzeResponse}

/**
 * 形態素解析処理を行うサービスです。
 */
trait MorphologicalService extends BaseService {

  /**
   * 引数で指定したテキストからタグを生成し、取得します。
   */
  @LambdaFunction(functionName="Morphological_analyze")
  def analyze(input: AnalyzeRequest): AnalyzeResponse
}

object MorphologicalService {
  val lambda = new AWSLambdaClient()
  lambda.configureRegion(Regions.AP_NORTHEAST_1)

  val service = LambdaInvokerFactory.build(classOf[MorphologicalService], lambda)

  def analyze(input: AnalyzeRequest): AnalyzeResponse = {
    service.analyze(input)
  }
}

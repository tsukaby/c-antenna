package com.tsukaby.c_antenna.util

import scala.concurrent.duration._

/**
 * 日付に関する操作を提供します。
 */
object TimeUtil {

  /**
   * 引数で指定した処理を行い、その結果と実行にかかった時間を返します。
   * @param a 処理
   * @tparam A 処理が返す型
   * @return 処理結果と時間のタプル
   */
  def time[A](a: => A) = {
    val now = System.nanoTime()
    val result = a
    val nanos = (System.nanoTime() - now) nanoseconds

    (result, nanos)
  }
}

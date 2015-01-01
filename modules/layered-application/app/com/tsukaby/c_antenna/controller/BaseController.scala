package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.Json4sFormatter
import play.api.mvc.Controller

/**
 * 全てのコントローラで利用する処理を提供する基底クラス
 */
trait BaseController extends Controller with Json4sFormatter {
}

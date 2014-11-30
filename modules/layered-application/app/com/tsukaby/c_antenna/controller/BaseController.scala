package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.MyJsonProtocol
import play.api.mvc.Controller

/**
 * 全てのコントローラで利用する処理を提供する基底クラス
 */
trait BaseController extends Controller with MyJsonProtocol {

}

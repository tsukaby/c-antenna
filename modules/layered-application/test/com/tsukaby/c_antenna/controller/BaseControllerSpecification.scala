package com.tsukaby.c_antenna.controller

import com.tsukaby.c_antenna.MyJsonProtocol
import org.specs2.mock.Mockito
import play.api.test.PlaySpecification

trait BaseControllerSpecification extends PlaySpecification with Mockito with MyJsonProtocol {
}

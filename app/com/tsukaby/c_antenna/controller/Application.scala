package com.tsukaby.c_antenna.controller

import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.{Dimension, OutputType}
import play.api.mvc.Controller

object Application extends Controller {

  private def getImage(url: String): Option[Array[Byte]] = {
    val driver: PhantomJSDriver = new PhantomJSDriver(new DesiredCapabilities())

    //driver.manage().window().setSize(new Dimension(1366, 768))
    driver.manage().window().setSize(new Dimension(400, 300))
    driver.get(url)
    val bytes: Array[Byte] = driver.getScreenshotAs(OutputType.BYTES)
    driver.quit()

    if (bytes == null || bytes.length == 0) {
      None
    } else {
      Option(bytes)
    }
  }

}

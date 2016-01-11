package com.tsukaby.c_antenna.service

import java.io.{File, FileOutputStream}
import org.apache.commons.codec.binary.Base64

import scala.language.higherKinds
import scala.sys.process._
import scala.util.{Try, Failure, Success, Random}

sealed trait OutputType[T] {
  def convertFrom(base64String: String): T
}
object Base64String extends OutputType[String] {
  override def convertFrom(base64String: String): String = base64String
}
object ByteArray extends OutputType[Array[Byte]] {
  override def convertFrom(base64String: String): Array[Byte] = Base64.decodeBase64(base64String)
}

trait PhantomJSDriverAlt {
  def getScreenshot[T](url: String, paper: String, outputType: OutputType[T]): Try[T] = {
    val rasterizeJsFileName = s"rasterize_${Random.nextInt()}.js"
    val rasterizeJs =
      """
        |"use strict";
        |var page = require('webpage').create(),
        |    system = require('system'),
        |    address, size, pageWidth, pageHeight;
        |
        |if (system.args.length < 2 || system.args.length > 4) {
        |    console.log('Usage: rasterize.js URL filename [paperwidth*paperheight|paperformat] [zoom]');
        |    console.log('  paper (pdf output) examples: "5in*7.5in", "10cm*20cm", "A4", "Letter"');
        |    console.log('  examples: "1920px" entire page, window width 1920px');
        |    console.log('                                   "800px*600px" window, clipped to 800x600');
        |    phantom.exit(1);
        |} else {
        |    address = system.args[1];
        |    // Suppress messages on console.
        |    page.onError = function(msg, trace) {
        |    };
        |    page.viewportSize = { width: 600, height: 600 };
        |    page.settings.resourceTimeout = 5000; // 5 seconds
        |    if (system.args.length > 2 && system.args[2].substr(-2) === "px") {
        |        size = system.args[2].split('*');
        |        if (size.length === 2) {
        |            pageWidth = parseInt(size[0], 10);
        |            pageHeight = parseInt(size[1], 10);
        |            page.viewportSize = { width: pageWidth, height: pageHeight };
        |            page.clipRect = { top: 0, left: 0, width: pageWidth, height: pageHeight };
        |        } else {
        |            console.log("size:", system.args[2]);
        |            pageWidth = parseInt(system.args[2], 10);
        |            pageHeight = parseInt(pageWidth * 3/4, 10); // it's as good an assumption as any
        |            console.log ("pageHeight:",pageHeight);
        |            page.viewportSize = { width: pageWidth, height: pageHeight };
        |        }
        |    }
        |    if (system.args.length > 3) {
        |        page.zoomFactor = system.args[3];
        |    }
        |    page.open(address, function (status) {
        |        if (status !== 'success') {
        |            console.log('Unable to load the address!');
        |            phantom.exit(1);
        |        } else {
        |            window.setTimeout(function () {
        |                var base64 = page.renderBase64('PNG');
        |                console.log(base64);
        |                phantom.exit();
        |            }, 200);
        |        }
        |    });
        |}
      """.stripMargin

    val fos = new FileOutputStream(rasterizeJsFileName)
    fos.write(rasterizeJs.getBytes("UTF-8"))
    fos.close()

    var res = ""
    val io = new ProcessIO(
      _ => (),
      stdout => {
        res = scala.io.Source.fromInputStream(stdout).getLines().mkString("")
        stdout.close()
      },
      _ => ())

    val p: ProcessBuilder = Process(List("phantomjs", rasterizeJsFileName, url, paper))
    val proc = p.run(io)
    val exitCode = proc.exitValue()

    if (exitCode == 0) {
      // success
      val base64String = res.replaceAll(System.lineSeparator(), "")
      val file = new File(rasterizeJsFileName)
      if (file.exists()) {
        file.delete()
      }
      Success(outputType.convertFrom(base64String))
    } else {
      // failure
      Failure(new IllegalStateException(s"Screenshot failure. url = $url"))
    }
  }
}

object PhantomJSDriverAlt extends PhantomJSDriverAlt

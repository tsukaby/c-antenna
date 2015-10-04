import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.tsukaby.c_antenna.lambda.Morphological
import org.specs2.mutable.Specification

class MorphologicalSpec extends Specification {

  "Morphological" >> {
    "#analyze" >> {
      "when gave text" >> {
        "it returns morphological text" in {
          val target = new Morphological()
          val request =
            """
              |{
              |  "text": "吾輩は猫である。名前はまだ無い。\nどこで生れたか頓（とん）と見當がつかぬ。何でも薄暗いじめじめした所でニヤーニヤー泣いて居た事丈は記憶して居る。"
              |}
            """.stripMargin
          val in = new ByteArrayInputStream(request.getBytes("UTF-8"))
          val out = new ByteArrayOutputStream()
          target.analyze(in, out)

          val expected = """{"tags":["當","頓","名前","ニヤーニヤー","丈","猫"]}"""
          val actual = new String(out.toByteArray, "UTF-8")

          actual must be_===(expected)
        }
      }
    }
  }
}

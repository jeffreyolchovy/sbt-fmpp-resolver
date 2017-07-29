package sbtfmpptemplate

import org.scalatest.{FlatSpec, Matchers}
import sbtbuildinfo.BuildInfoResult

class TddRendererSpec extends FlatSpec with Matchers {

  behavior of "TddRenderer"

  it should "render numeric values" in {
    TddRenderer.render(1) shouldBe "1"
    TddRenderer.render(1L) shouldBe "1"
    TddRenderer.render(1D) shouldBe "1.0"
    TddRenderer.render(1.2) shouldBe "1.2"
  }

  it should "render boolean values" in {
    TddRenderer.render(true) shouldBe "true"
    TddRenderer.render(false) shouldBe "false"
  }

  it should "render symbols" in {
    TddRenderer.render('foo) shouldBe "\"foo\""
  }

  it should "render tuples as hashes" in {
    TddRenderer.render("a" -> "b") shouldBe "{\"a\": \"b\"}"
  }

  it should "render maps as hashes" in {
    TddRenderer.render(Map("a" -> "b", "c" -> "d")) shouldBe "{\"a\": \"b\", \"c\": \"d\"}"
  }

  it should "render sequences as comma separated values" in {
    TddRenderer.render(Seq("a", "b", "c")) shouldBe "\"a\", \"b\", \"c\""
  }

  it should "render options" in {
    TddRenderer.render(Some("a")) shouldBe "\"a\""
    TddRenderer.render(None) shouldBe "\"\""
  }

  it should "render null as empty" in {
    TddRenderer.render(null) shouldBe "\"\""
  }

  implicit def pair2result[A](pair: (String, A)): BuildInfoResult = {
    BuildInfoResult(pair._1, pair._2, null)
  }
}

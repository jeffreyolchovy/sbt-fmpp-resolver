package sbtfmppresolver

import org.scalatest.{FlatSpec, Matchers}

class ArgsSpec extends FlatSpec with Matchers {

  behavior of "Args"

  it should "determine if an option is set when given a set of available options" in {
    val args = Args.fromString("-a -b -x foo")
    args.containsAny("-a") shouldBe true
    args.containsAny("-b") shouldBe true
    args.containsAny("-x") shouldBe true
    args.containsAny("-y") shouldBe false
    args.containsAny("-a", "-b") shouldBe true
    args.containsAny("-a", "-b", "-x") shouldBe true
    args.containsAny("-a", "-b", "-x", "-y") shouldBe true
    args.containsAny("-y", "-z") shouldBe false
  }

  it should "replace an option value with a given value by option key" in {
    val args = Args.fromString("--foo bar --baz qux")
    val args1 = args.replace("--foo", "BAR")
    args1.valueOf("--foo") shouldBe "BAR"
  }
}

package sbtfmppresolver

import org.scalatest.{FlatSpec, Matchers}

class ArgsParserSpec extends FlatSpec with Matchers {

  behavior of "ArgsParser (underlying parsers)"

  it should "parse words" in {
    val input = "foo bar baz"
    val result = ArgsParser.parse(ArgsParser.unquotedStringValue, input)
    result.successful shouldBe true
  }

  it should "parse words (ignoring the following key)" in {
    val input = "foo --bar"
    val result = ArgsParser.parse(ArgsParser.unquotedStringValue, input)
    result.successful shouldBe true
    result.get shouldBe "foo"
  }

  it should "parse values (ignoring the following key)" in {
    val input = "foo --bar"
    val result = ArgsParser.parse(ArgsParser.value, input)
    result.successful shouldBe true
    result.get shouldBe "foo"
  }

  it should "parse key-value, binary args" in {
    val input = "--key2 value"
    val result = ArgsParser.parse(ArgsParser.arg, input)
    result.successful shouldBe true
  }

  it should "parse key-only, unary args" in {
    val input = "-k"
    val result = ArgsParser.parse(ArgsParser.arg, input)
    result.successful shouldBe true
  }

  behavior of "ArgsParser"

  it should "parse binary args" in {
    val input = "--key value"
    val args = ArgsParser(input)
    args.valueOf("--key") shouldBe "value"
  }

  it should "parse binary args with whitespace" in {
    val input = "--key val ue"
    val args = ArgsParser(input)
    args.valueOf("--key") shouldBe "val ue"
  }

  it should "parse binary short args" in {
    val input = "-k value"
    val args = ArgsParser(input)
    args.valueOf("-k") shouldBe "value"
  }

  it should "parse unary args" in {
    val input = "--key"
    val args = ArgsParser(input)
    args.contains("--key") shouldBe true
    intercept[IllegalArgumentException] {
      args.valueOf("--key")
    }
  }

  it should "parse multiple, successive unary args" in {
    val input = "--key1 --key2"
    val args = ArgsParser(input)
    args.contains("--key1") shouldBe true
    args.contains("--key2") shouldBe true
  }

  it should "parse unary and binary args in the same input string" in {
    val input = "--key1 --key2 value"
    val args = ArgsParser(input)
    args.contains("--key1") shouldBe true
    args.valueOf("--key2") shouldBe "value"
  }

  it should "parse binary and unary args in the same input string" in {
    val input = "--key1 value --key2"
    val args = ArgsParser(input)
    args.valueOf("--key1") shouldBe "value"
    args.contains("--key2") shouldBe true
  }

  it should "parse binary (with whitespace) and unary args in the same input string" in {
    val input = "--key1 val ue --key2"
    val args = ArgsParser(input)
    args.valueOf("--key1") shouldBe "val ue"
    args.contains("--key2") shouldBe true
  }

  it should "parse short and long args in the same input string" in {
    val input = "-k value1 --key2 value2"
    val args = ArgsParser(input)
    args.valueOf("-k") shouldBe "value1"
    args.valueOf("--key2") shouldBe "value2"
  }

  it should "parse args with multiple associative arguments" in {
    val input = "--foo FOO --bar BAR --baz BAZ"
    val output = ArgsParser(input)
    output.contains("--foo") shouldBe true
    output.valueOf("--foo") shouldBe "FOO"
    output.contains("--bar") shouldBe true
    output.valueOf("--bar") shouldBe "BAR"
    output.contains("--baz") shouldBe true
    output.valueOf("--baz") shouldBe "BAZ"
  }

  it should "parse args with multiple non-associative arguments" in {
    val input = "--foo --bar --baz"
    val output = ArgsParser(input)
    output.contains("--foo") shouldBe true
    output.contains("--bar") shouldBe true
    output.contains("--baz") shouldBe true
  }

  it should "parse args with mixed-style arguments" in {
    val associativeArgFirst = "--foo FOO --bar"
    val associativeArgSecond = "--bar --foo FOO"
    val output1 = ArgsParser(associativeArgFirst)
    val output2 = ArgsParser(associativeArgSecond)
    output1.underlying should contain theSameElementsAs output2.underlying
    output1.contains("--foo") shouldBe true
    output1.valueOf("--foo") shouldBe "FOO"
    output1.contains("--bar") shouldBe true
  }

  it should "parse args that have string values enclosed in single quotes" in {
    val input = "--foo 'abc def ghi'"
    val output = ArgsParser(input)
    output.contains("--foo") shouldBe true
    output.valueOf("--foo") shouldBe "'abc def ghi'"
  }

  it should "parse args that have string values enclosed in double quotes" in {
    val input = "--foo \"abc def ghi\""
    val output = ArgsParser(input)
    output.contains("--foo") shouldBe true
    output.valueOf("--foo") shouldBe "\"abc def ghi\""
  }

  it should "parse args that have string values enclosed in single quotes which contain escaped quotes" in {
    val input = "--foo '\\'twas bar'"
    val output = ArgsParser(input)
    output.contains("--foo") shouldBe true
    output.valueOf("--foo") shouldBe "'\\'twas bar'"
  }

  it should "parse args that have multiple string values enclosed in single quotes" in {
    val input = "--foo 'foo' --bar ''"
    val output = ArgsParser(input)
    output.valueOf("--foo") shouldBe "'foo'"
    output.contains("--bar") shouldBe true
  }

  it should "parse args that have multiple string values enclosed in single quotes, interspersed with other values" in {
    val input = "--props classpath:application-dev.properties --foo 'foo' --qux quxx --bar ''"
    val output = ArgsParser(input)
    output.valueOf("--props") shouldBe "classpath:application-dev.properties"
    output.valueOf("--foo") shouldBe "'foo'"
    output.valueOf("--qux") shouldBe "quxx"
    output.contains("--bar") shouldBe true
  }

  it should "parse args that have string values enclosed in double quotes which contain escaped quotes" in {
    val input = "--foo \"\\\"bar\\\"\""
    val output = ArgsParser(input)
    output.contains("--foo") shouldBe true
    output.valueOf("--foo") shouldBe "\"\\\"bar\\\"\""
  }
}

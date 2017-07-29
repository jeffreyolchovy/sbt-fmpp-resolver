package sbtfmppresolver

import scala.util.parsing.combinator.RegexParsers

object ArgsParser extends RegexParsers {

  override def skipWhitespace = false

  private val EOL = """\z""".r.withFailureMessage("end-of-input expected")

  private val keyName = excludingCharClass(" ", description = Some("whitespace"))

  private val keyShortPrefix = "-"

  private val keyLongPrefix = "--"

  private val keyPrefix = keyLongPrefix | keyShortPrefix

  val key = (keyPrefix ~ keyName).withFailureMessage(s"argument name prefixed with '$keyShortPrefix' or '$keyLongPrefix' expected") ^^ {
    case prefix ~ key => prefix + key
  }

  val singleQuotedStringValue = ("'" + """([^'\p{Cntrl}\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*""" + "'").r

  val doubleQuotedStringValue = ("\"" + """([^"\p{Cntrl}\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*""" + "\"").r

  val unquotedStringValue: Parser[String] = not(keyPrefix) ~> repsep("""\S+""".r, whiteSpace <~ not(keyPrefix)) ^^ {
    case values => values.mkString(" ")
  }

  val value = (singleQuotedStringValue | doubleQuotedStringValue | unquotedStringValue)

  val arg = opt(key) ~ opt(whiteSpace ~> value) ^^ {
    case None ~ None => None
    case None ~ Some(value) => Some("" -> value)
    case Some(key) ~ None => Some(key, "")
    case Some(key) ~ Some(value) => Some(key -> value)
  }

  val parser = repsep(arg, whiteSpace) <~ EOL

  def apply(input: String): Args = {
    parse(parser, input) match {
      case Success(args, _) =>
        val underlying = args.flatten
        new Args(underlying)
      case NoSuccess(msg, _) =>
        throw new RuntimeException(s"Can not parse CLI args from '$input': $msg")
    }
  }

  private def excludingCharClass(charClass: String, description: Option[String] = None): Parser[String] = {
    ("""([^""" + charClass + """])*""").r.withFailureMessage(description.getOrElse(s"[$charClass]") + " not expected")
  }
}

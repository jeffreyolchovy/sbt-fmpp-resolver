package sbtfmpptemplate

import sbtbuildinfo._

case object TddRenderer extends BuildInfoRenderer {

  val fileType = BuildInfoType.Resource

  val extension = "fmpp"

  val header = Seq("data: {")

  val footer = Seq("}")

  def renderKeys(results: Seq[BuildInfoResult]): Seq[String] = {
    header ++
    results.map { result => s"""\t"${result.identifier}": ${render(result.value)}""" } ++
    footer
  }

  def render(value: Any): String = {
    value match {
      case n: Int         => n.toString
      case n: Long        => n.toString
      case d: Double      => d.toString
      case b: Boolean     => b.toString
      case sym: Symbol    => quote(sym.name)
      case (k, v)         => render(Map(k -> v))
      case map: Map[_, _] => map.toSeq.map { case (k, v) => render(k) + ": " + render(v) }.mkString("{", ", ", "}")
      case seq: Seq[_]    => seq.map(render).mkString(", ")
      case Some(value)    => render(value)
      case None | null    => quote("")
      case other          => quote(encodeStringLiteral(other.toString))
    }
  }

  private def quote(value: String): String = {
    "\"" + value + "\""
  }

  private def encodeStringLiteral(value: String): String = {
    value.replace("\\","\\\\")
      .replace("\n","\\n")
      .replace("\b","\\b")
      .replace("\r","\\r")
      .replace("\t","\\t")
      .replace("\'","\\'")
      .replace("\f","\\f")
      .replace("\"","\\\"")
  }
}

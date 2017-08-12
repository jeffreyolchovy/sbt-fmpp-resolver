package sbtfmppresolver

case class Args(underlying: Seq[(String, String)]) {

  def get(key: String): Option[(String, String)] = {
    underlying.find(_._1 == key)
  }

  def valueOf(key: String): String = {
    get(key).map(_._2) match {
      case None | Some("") => throw new IllegalArgumentException(s"Please provide a value for $key")
      case Some(value) => value
    }
  }

  def contains(key: String): Boolean = {
    underlying.map(_._1).contains(key)
  }

  def containsAny(keys: String*): Boolean = {
    val targetKeys = keys.toSet
    val currentKeys = underlying.map(_._1).toSet
    (targetKeys & currentKeys).nonEmpty
  }

  def replace(key: String, value: String): Args = {
    Args(
      underlying.map {
        case (currentKey, _) if key == currentKey => (key, value)
        case keyValue => keyValue
      }
    )
  }

  def toArray: Array[String] = {
    underlying.flatMap { case (key, value) => Array(key, value) }.toArray
  }
}

object Args {

  def apply(args: Array[String]): Args = {
    fromString(args.mkString(" "))
  }

  def fromString(args: String): Args = {
    ArgsParser(args)
  }
}

package sbtfmppresolver

import java.net.URI
import scala.util.matching.Regex
import sbt.template.TemplateResolver
import fmpp.tools.CommandLine

class FmppTemplateResolver extends TemplateResolver {

  import FmppTemplateResolver._

  /* Accept iff -C|--configuration or -S|--source-root present
   *
   * Configuration files must exist on the local file system
   *
   * Source root can be an absolute or relative path on the local file system,
   * or a URI to a remote git repository
   */
  def isDefined(args0: Array[String]): Boolean = {
    val args = Args(args0)
    hasConfigurationArg(args) || hasSupportedSourceArg(args)
  }

  def run(args0: Array[String]): Unit = {
    val args = Args(args0)
    val args1 = getSourceArg(args) match {
      // no source directory arg given, no arg remapping necessary
      case None => args0

      // source directory arg given, arg value may require remapping
      case Some((key, value)) => value match {
        // source directory is a remote resource,
        // copy remote resource to local file system,
        // and remap the source directory arg to point to the local path
        case GitUri(uri, _, ref) =>
          val file = GitUtils.copyToLocal(uri, Option(ref)).get
          val newValue = file.getAbsolutePath
          args.replace(key, newValue).toArray

        // source directory is not a remote resource, no arg remapping necessary
        case _  => args0
      }
    }

    CommandLine.execute(args1, null, null)
  }
}

object FmppTemplateResolver {

  val ShortSourceKey = "-S"

  val LongSourceKey = "--source-root"

  val ShortConfigurationKey = "-C"

  val LongConfigurationKey = "--configuration"

  val LocalUri = """^(\S+)(?:/)?$""".r

  object GitUri {
    val NativeUri = uriPattern("git[@|://]")
    val HttpsUri  = uriPattern("https://")
    val HttpUri   = uriPattern("http://")
    val SshUri    = uriPattern("ssh://")

    def uriPattern(protocolPattern: String): Regex = {
      ("^(" + protocolPattern + "[^#]*)(#(.+))?$").r
    }

    def unapplySeq(s: Any): Option[List[String]] = {
      NativeUri.unapplySeq(s) orElse
      HttpsUri.unapplySeq(s) orElse
      HttpUri.unapplySeq(s) orElse
      SshUri.unapplySeq(s)
    }
  }

  def hasConfigurationArg(args: Args): Boolean = {
    args.containsAny(ShortConfigurationKey, LongConfigurationKey)
  }

  def hasSupportedSourceArg(args: Args): Boolean = {
    getSourceArg(args) match {
      case Some((_, value)) => value match {
        case GitUri(_, _, _) => true
        case LocalUri(_) => true
        case _ => false
      }
      case None => false
    }
  }

  def getSourceArg(args: Args): Option[(String, String)] = {
    args.get(ShortSourceKey) orElse args.get(LongSourceKey)
  }
}

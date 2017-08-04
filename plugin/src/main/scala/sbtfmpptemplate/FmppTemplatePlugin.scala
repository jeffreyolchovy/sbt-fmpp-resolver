package sbtfmpptemplate

import sbt._
import sbt.Def.Setting
import sbt.Keys._
import sbt.complete.DefaultParsers._
import sbt.plugins.CorePlugin
import sbtfmppresolver.FmppTemplateResolver

object FmppTemplatePlugin extends AutoPlugin {

  override def requires = CorePlugin

  override def trigger = allRequirements

  object autoImport {
    val FmppTemplateKeys = sbtfmpptemplate.FmppTemplateKeys
    val fmppResolver = FmppTemplateKeys.fmppResolver
    val fmpp = FmppTemplateKeys.fmpp
  }

  import autoImport._

  override lazy val globalSettings: Seq[Setting[_]] = Seq(
    fmppResolver := new FmppTemplateResolver,
    fmpp := {
      val args = spaceDelimited("<args>").parsed.toArray
      val resolver = fmppResolver.value
      if (resolver.isDefined(args)) {
        resolver.run(args)
      }
    },
    templateResolverInfos += TemplateResolverInfo(
      "com.github.jeffreyolchovy" %% "sbt-fmpp-resolver" % BuildInfo.version cross(CrossVersion.binary),
      "sbtfmppresolver.FmppTemplateResolver"
    )
  )
}

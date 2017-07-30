package sbtfmpptemplate

import sbt._
import sbt.Def.Setting
import sbt.Keys._
import sbt.plugins.CorePlugin

object FmppTemplatePlugin extends AutoPlugin {

  override def requires = CorePlugin

  override def trigger = allRequirements

  override lazy val globalSettings: Seq[Setting[_]] = Seq(
    templateResolverInfos += TemplateResolverInfo(
      "com.github.jeffreyolchovy" %% "sbt-fmpp-resolver" % BuildInfo.version cross(CrossVersion.binary),
      "sbtfmppresolver.FmppTemplateResolver"
    )
  )
}

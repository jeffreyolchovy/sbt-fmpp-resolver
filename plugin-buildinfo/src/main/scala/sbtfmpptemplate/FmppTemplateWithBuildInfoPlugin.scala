package sbtfmpptemplate

import sbt._
import sbt.Def.Setting
import sbt.Keys._
import sbtbuildinfo.{BuildInfoKeys, BuildInfoPlugin}

object FmppTemplateWithBuildInfoPlugin extends AutoPlugin {

  override def requires = FmppTemplatePlugin && BuildInfoPlugin

  override def trigger = allRequirements

  override def projectSettings: Seq[Setting[_]] = Seq(
    BuildInfoKeys.buildInfoRenderer := TddRenderer
  )
}

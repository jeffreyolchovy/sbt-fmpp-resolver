package sbtfmpptemplate

import sbt._
import sbt.template.TemplateResolver

object FmppTemplateKeys {
  val fmppResolver = settingKey[TemplateResolver]("An sbt TemplateResolver for FreeMarker templates")
  val fmpp = inputKey[Unit]("Execute the FMPP command-line front-end")
}

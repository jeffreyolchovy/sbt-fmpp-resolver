addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")

{
  sys.props.get("plugin.version") match {
    case Some(pluginVersion) =>
      addSbtPlugin("com.github.jeffreyolchovy" % "sbt-fmpp-template" % pluginVersion)
    case None =>
      sys.error(
        """
        |The system property 'plugin.version' is not defined.
        |Specify this property using the scriptedLaunchOpts -D.
        """.stripMargin.trim
      )
  }
}

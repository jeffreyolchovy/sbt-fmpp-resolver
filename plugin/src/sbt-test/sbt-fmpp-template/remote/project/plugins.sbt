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

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"

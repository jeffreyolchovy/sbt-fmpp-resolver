import Publishing._

inThisBuild(
  Seq(
    organization := "com.github.jeffreyolchovy",
    version := "0.1.0rc1-SNAPSHOT",
    scalacOptions ++= Seq("-deprecation"),
    licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0")),
    developers := List(
      Developer("jeffreyolchovy", "Jeffrey Olchovy", "@jaeo", url("https://github.com/jeffreyolchovy"))
    )
  )
)

lazy val root = (project in file("."))
  .settings(noopPublishSettings: _*)
  .aggregate(resolver, plugin, pluginWithBuildInfo)
  .enablePlugins(CrossPerProjectPlugin)

lazy val plugin: Project = (project in file("plugin"))
  .settings(commonPluginSettings: _*)
  .settings(pluginPublishSettings: _*)
  .settings(
    name := "sbt-fmpp-template",
    buildInfoKeys := Seq[BuildInfoKey](version),
    buildInfoPackage := "sbtfmpptemplate",
    update := update.dependsOn(crossPublishLocal in resolver).value,
    crossPublishLocal := crossPublishLocalImpl.value
  )
  .dependsOn(resolver)
  .enablePlugins(BuildInfoPlugin)

lazy val pluginWithBuildInfo = (project in file("plugin-buildinfo"))
  .settings(commonPluginSettings: _*)
  .settings(noopPublishSettings: _*)
  .settings(
    name := "sbt-fmpp-template-buildinfo",
    update := update.dependsOn(crossPublishLocal in plugin).value,
    libraryDependencies ++= {
      val currentSbtVersion = (sbtBinaryVersion in pluginCrossBuild).value
      Seq(
        Defaults.sbtPluginExtra(
          "com.eed3si9n" % "sbt-buildinfo" % "0.7.0",
          currentSbtVersion,
          scalaBinaryVersion.value
        ),
        Defaults.sbtPluginExtra(
          (organization in plugin).value % (name in plugin).value % version.value,
          currentSbtVersion,
          scalaBinaryVersion.value
        )
      )
    }
  )

lazy val resolver = (project in file("resolver"))
  .settings(libraryPublishSettings: _*)
  .settings(
    name := "sbt-fmpp-resolver",
    scalaVersion := "2.10.6",
    crossScalaVersions := Seq("2.10.6", "2.12.3"),
    libraryDependencies ++= Seq(
      "org.scala-sbt" % "template-resolver" % "0.1",
      "net.sourceforge.fmpp" % "fmpp" % "0.9.15",
      "commons-io" % "commons-io" % "2.5",
      "org.eclipse.jgit" % "org.eclipse.jgit.pgm" % "3.7.0.201502260915-r",
      "org.scalatest" %% "scalatest" % "3.0.1" % Test
    ),
    libraryDependencies := parserCombinators(scalaVersion.value).fold(libraryDependencies.value) {
      libraryDependencies.value :+ _
    },
    crossPublishLocal := crossPublishLocalImpl.value
  )

val commonPluginSettings = scriptedSettings ++ Seq(
  scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value),
  scriptedBufferLog := false,
  sbtPlugin := true,
  crossSbtVersions := Seq("0.13.16", "1.0.0-RC3"),
  scalaVersion := {
    val currentSbtVersion = (sbtVersion in pluginCrossBuild).value
    CrossVersion.partialVersion(currentSbtVersion) match {
      case Some((0, 13)) => "2.10.6"
      case Some((1, _))  => "2.12.3"
      case _             => sys.error(s"Unsupported sbt version: $currentSbtVersion")
    }
  },
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % Test
)

def parserCombinators(scalaVersion: String): Option[ModuleID] = {
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, 10)) => None
    case _ => Some("org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4")
  }
}

val crossPublishLocal = taskKey[Unit]("+publishLocal implemented as a task")
val crossPublishLocalImpl = Def.task[Unit] {
  val s = streams.value
  val projectId = thisProject.value.id
  (crossScalaVersions.value).flatMap { version =>
    s.log.info(s"Locally publishing artifact for $projectId with Scala version $version")
    Seq(
      Command.process(s"++$version", _: State),
      Command.process(s"$projectId/publishLocal", _: State)
    )
  }.foldLeft(state.value) { (state, cmd) =>
    cmd(state)
  }
}

import sbt._
import sbt.Keys._
import bintray.BintrayKeys._

object Publishing {

  val noopPublishSettings = Seq(
    packagedArtifacts in RootProject(file(".")) := Map.empty,
    publish := (),
    publishLocal := (),
    publishArtifact := false,
    publishTo := None
  )

  val commonPublishSettings = Seq(
    autoAPIMappings := true,
    pomIncludeRepository := { _ => false },
    publishArtifact in Test := false,
    publishArtifact in (Compile, packageDoc) := true,
    publishArtifact in (Compile, packageSrc) := true
  )

  val pluginPublishSettings = commonPublishSettings ++ Seq(
    bintrayRepository := "sbt-plugins"
  )

  val libraryPublishSettings = commonPublishSettings ++ Seq(
    bintrayRepository := "maven",
    bintrayPackage := "sbt-fmpp-resolver",
    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    homepage := Some(new URL("https://github.com/jeffreyolchovy/sbt-fmpp-resolver")),
    pomExtra := {
      <scm>
        <url>https://github.com/jeffreyolchovy/sbt-fmpp-resolver</url>
        <connection>scm:git:git://github.com/jeffreyolchovy/sbt-fmpp-resolver.git</connection>
      </scm>
    }
  )
}

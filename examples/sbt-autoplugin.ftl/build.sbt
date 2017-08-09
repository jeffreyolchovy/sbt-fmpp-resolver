name := "${name}"

organization := "${organization}"

scalaVersion := "${scalaVersion}"

sbtVersion := "1.0.0-RC3"

sbtPlugin := true

ScriptedPlugin.scriptedSettings

scripted := scripted.dependsOn(publishLocal).evaluated

scriptedLaunchOpts ++= Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dplugin.version=" + version.value)

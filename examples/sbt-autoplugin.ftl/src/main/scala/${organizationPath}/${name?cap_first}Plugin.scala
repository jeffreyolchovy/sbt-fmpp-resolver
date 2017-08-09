package ${organization}

import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

object ${name?cap_first}Plugin extends AutoPlugin {

  override def requires = JvmPlugin

  override def trigger = allRequirements

  object autoImport {
    val exampleSetting = settingKey[String]("A setting that is automatically imported into the build")
    val exampleTask = taskKey[String]("A task that is automatically imported into the build")
  }

  import autoImport._

  override lazy val projectSettings = Seq(
    exampleSetting := "An example",
    exampleTask := "Computed using: " + exampleSetting.value
  )
}

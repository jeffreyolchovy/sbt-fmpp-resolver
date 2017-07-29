package sbtfmppresolver

import java.io.File
import java.nio.file.Files
import java.net.URI
import scala.util.Try

object GitUtils {

  def newTempDirectory: File = {
    val path = Files.createTempDirectory("gitrepo-")
    path.toFile
  }

  def parseUri(value: String): (String, Option[String]) = {
    val index = value.indexOf("#")
    if (index == -1) {
      (value, None)
    } else {
      (value.substring(0, index), Some(value.substring(index, value.length - 1)))
    }
  }

  def copyToLocal(uri: String, target: File = newTempDirectory): Try[File] = {
    parseUri(uri) match {
      case (uri, Some(ref)) => for {
        branches  <- GitInteractor.getRemoteBranches(uri)
        tags      <- GitInteractor.getRemoteTags(uri)
        hasBranch  = branches.contains(ref)
        hasTag     = tags.contains(ref)
        _         <- GitInteractor.cloneRepository(uri, target) if hasBranch || hasTag
        _         <- GitInteractor.checkoutRef(target, ref, hasBranch)
      } yield {
        target
      }

      case (uri, None) => for {
        _       <- GitInteractor.cloneRepository(uri, target)
        branch  <- GitInteractor.getDefaultBranch(target)
        _       <- GitInteractor.checkoutBranch(target, branch)
      } yield {
        target
      }
    }
  }
}

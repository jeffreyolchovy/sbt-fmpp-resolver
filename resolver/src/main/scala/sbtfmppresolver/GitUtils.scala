package sbtfmppresolver

import java.io.File
import scala.util.Try
import org.apache.commons.io.FileUtils

object GitUtils {

  def copyToLocal(uri: String, ref: Option[String] = None, target: File = createTempDirectory): Try[File] = {
    ref match {
      case Some(ref) => for {
        branches  <- GitInteractor.getRemoteBranches(uri)
        tags      <- GitInteractor.getRemoteTags(uri)
        hasBranch  = branches.contains(ref)
        hasTag     = tags.contains(ref)
        _         <- GitInteractor.cloneRepository(uri, target) if hasBranch || hasTag
        _         <- GitInteractor.checkoutRef(target, ref, hasBranch)
      } yield {
        target
      }

      case None => for {
        _       <- GitInteractor.cloneRepository(uri, target)
        branch  <- GitInteractor.getDefaultBranch(target)
        _       <- GitInteractor.checkoutBranch(target, branch)
      } yield {
        target
      }
    }
  }

  private def createTempDirectory: File = {
    val dir = new File(FileUtils.getTempDirectory, "gitrepo-" + System.nanoTime)
    dir.mkdirs()
    sys.addShutdownHook {
      FileUtils.deleteQuietly(dir)
    }
    dir
  }
}

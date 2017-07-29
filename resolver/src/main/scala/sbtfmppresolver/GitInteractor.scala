/*
 * Original implementation (C) 2010-2015 Nathan Hamblen and contributors
 * Adapted and extended in 2016 by foundweekends project
 * Adapted and extended in 2017 by Jeffrey Olchovy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sbtfmppresolver

import java.io.File

import org.eclipse.jgit.api.errors.TransportException
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.api.Git

import scala.util.{Failure, Success, Try}
import scala.collection.JavaConverters._

object GitInteractor {

  case class TransportError(message: String) extends RuntimeException(message)

  CredentialsProvider.setDefault(ConsoleCredentialsProvider)

  def cloneRepository(url: String, dest: File): Try[Unit] = Try {
    Git
      .cloneRepository()
      .setURI(url)
      .setDirectory(dest)
      .setCredentialsProvider(ConsoleCredentialsProvider)
      .call()
      .close()
  }

  def getRemoteBranches(url: String): Try[Seq[String]] = {
    Try(Git.lsRemoteRepository().setRemote(url).setHeads(true).setTags(false).call()) map { refs =>
      refs.asScala.map(r => r.getName.stripPrefix("refs/heads/")).toSeq
    } recoverWith {
      case e: TransportException => Failure(TransportError(e.getMessage))
    }
  }

  def getRemoteTags(url: String): Try[Seq[String]] = {
    Try(Git.lsRemoteRepository().setRemote(url).setHeads(false).setTags(true).call()) map { refs =>
      refs.asScala.map(r => r.getName.stripPrefix("refs/tags/")).toSeq
    } recoverWith {
      case e: TransportException => Failure(TransportError(e.getMessage))
    }
  }

  def getDefaultBranch(repository: File): Try[String] = Try {
    val git = Git.open(repository)
    try {
      val refs = git.getRepository.getAllRefs.asScala
      // We assume we have freshly cloned repository with origin set up to clone URL
      // Symref HEAD will point to default remote branch.
      val symRefs = refs.filter(_._2.isSymbolic)
      val head    = symRefs("HEAD")
      head.getTarget.getName.stripPrefix("refs/heads/")
    } finally git.close()
  }

  def checkoutRef(repository: File, ref: String, isBranch: Boolean): Try[Unit] = {
    if (isBranch) {
      checkoutBranch(repository, ref)
    } else {
      checkoutTag(repository, ref)
    }
  }

  def checkoutBranch(repository: File, branch: String): Try[Unit] = {
    val git = Git.open(repository)
    if (git.getRepository.getBranch == branch) Success(())
    else {
      val checkoutCommand = git.checkout().setName(branch)
      Try {
        checkoutCommand.setCreateBranch(true).setStartPoint("origin/" + branch).call()
      } map { _ =>
        git.close()
      }
    }
  }

  def checkoutTag(repository: File, tag: String): Try[Unit] = {
    val git = Git.open(repository)
    Try(git.checkout().setName(tag).call()).map(_ => git.close())
  }
}

/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ote.version.git;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.osee.ote.version.FileVersion;

/**
 * @author Michael P. Masterson
 */
public class GitFileVersion implements FileVersion {

   private final RevCommit commit;
   private final boolean modified;

   GitFileVersion(GitVersion gitVersion) throws NoHeadException, IOException, GitAPIException{
      this(gitVersion.getLastCommit(), gitVersion.getModified());
      if(commit == null){
         throw new IOException("Not a git repo");
      }
   }

   GitFileVersion(RevCommit commit, boolean modified) {
      this.commit = commit;
      this.modified = modified;
   }

   @Override
   public String getLastChangedRevision() {
      return commit.getId().getName();
   }

   @Override
   public String getURL() {
      return null;
   }

   @Override
   public String getVersionControlSystem() {
      return "GIT";
   }

   @Override
   public String getModifiedFlag() {
      return String.valueOf(modified);
   }

   @Override
   public String getLastModificationDate() {
      return commit.getAuthorIdent().getWhen().toString();
   }

   @Override
   public String getLastAuthor() {
      return commit.getAuthorIdent().getName();
   }

}

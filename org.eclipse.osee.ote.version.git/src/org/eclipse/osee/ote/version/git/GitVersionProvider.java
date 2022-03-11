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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.version.FileVersion;
import org.eclipse.osee.ote.version.FileVersionInformationProvider;

/**
 * @author Michael P. Masterson
 */
public class GitVersionProvider implements FileVersionInformationProvider {

   @Override
   public void getFileVersions(List<File> files, Map<File, FileVersion> versions) {
      GitVersions gitVersions = new GitVersions(files);
      Map<File, GitVersion> commits = gitVersions.getLastCommits();
      for (Entry<File, GitVersion> entry : commits.entrySet()) {
         try {
            versions.put(entry.getKey(), new GitFileVersion(entry.getValue()));
         } catch (NoHeadException ex) {
            OseeLog.log(this.getClass(), Level.SEVERE, ex.toString(), ex);
         } catch (IOException ex) {
            OseeLog.log(this.getClass(), Level.SEVERE, ex.toString(), ex);
         } catch (GitAPIException ex) {
            OseeLog.log(this.getClass(), Level.SEVERE, ex.toString(), ex);
         }
      }
   }

}

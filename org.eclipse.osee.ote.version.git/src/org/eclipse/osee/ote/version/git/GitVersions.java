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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

/**
 * @author Michael P. Masterson
 */
public class GitVersions extends GitVersionBase {

   private final List<File> files;

   public GitVersions(List<File> files) {
      this.files = files;
   }


   public  Map<File, GitVersion> getLastCommits() {
      Map<File, GitVersion> commits = new HashMap<>();
      Map<File, List<File>> gitToFiles = new HashMap<>();
      for(File file:files){
         if(!file.exists()){
            continue;
         }
         File gitFolder = findGitDirUp(file);
         if(gitFolder == null){
            continue;
         }
         List<File> gitfiles = gitToFiles.get(gitFolder);
         if(gitfiles == null){
            gitfiles = new ArrayList<>();
            gitToFiles.put(gitFolder, gitfiles);
         }
         gitfiles.add(file);
      }
      for(Entry<File, List<File>> entry:gitToFiles.entrySet()){
         try{
            Repository repository = buildRepository(entry.getKey());
            Git git = new Git(repository);

            for(File gitfile:entry.getValue()){
               GitVersion version = new GitVersion(gitfile);
               commits.put(gitfile, version);
            }
         }catch(IOException e){
            e.printStackTrace();
         }
      }
      return commits;
   }
}

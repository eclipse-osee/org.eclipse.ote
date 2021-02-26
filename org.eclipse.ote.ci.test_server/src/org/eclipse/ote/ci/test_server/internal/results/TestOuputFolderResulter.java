/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.ote.ci.test_server.internal.results;

import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Andy Jury
 */
public class TestOuputFolderResulter {
   public void processOutput(File outfileFolder) {
      try {
         File briefResultFile = new File(outfileFolder.getAbsolutePath() + File.separatorChar + "AllResults.txt");
         if (briefResultFile.exists()) {
            briefResultFile.delete();
         }
         ReentrantLock briefFileLock = new ReentrantLock();
         FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
               return name.endsWith("tmo");
            }
         };
         for (File outfile : outfileFolder.listFiles(filter)) {
            try {
               JUnitResultFormatWriter resultWriter =
                  new JUnitResultFormatWriter(new File(outfile.getAbsolutePath() + ".JUNIT.xml"));
               BriefResultWriter briefResultWriter = new BriefResultWriter(briefResultFile, briefFileLock);
               OutFileResultProcessor convert = new OutFileResultProcessor(outfile);
               convert.addResultWriter(resultWriter);
               convert.addResultWriter(briefResultWriter);
               convert.run();
            } catch (Throwable th) {
               th.printStackTrace();
            }
         }
      } catch (Exception ex) {
         System.out.println("Could not process outfiles in " + outfileFolder.getPath());
      }
   }
}

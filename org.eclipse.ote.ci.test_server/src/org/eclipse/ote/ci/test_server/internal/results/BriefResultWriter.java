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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * Appends the test suite name and its pass/fail state to a file.
 * 
 * @author David N. Phillips
 * @author Andy Jury
 */
public class BriefResultWriter implements ITestResultWriter {
   private File destination;
   private Lock fileAppendLock;

   public BriefResultWriter(File destination, Lock fileAppendLock) {
      this.destination = destination;
      this.fileAppendLock = fileAppendLock;
   }

   @Override
   public void process(String sourceName, List<TestCaseInfo> testCases) {
      final StringBuilder sb = new StringBuilder();
      int tests = 0;
      int failures = 0;
      int failedSuites = 0;
      int totalSuites = 1; // include this suite

      for (TestCaseInfo info : testCases) {
         tests += info.testPointInfo().size();
         failures += info.getFails();
      }
      if (failures != 0) {
         failedSuites++;
      }

      sb.append(failures == 0 ? "PASS" : "FAIL");
      sb.append(" - Test : " + sourceName);
      sb.append("\" - TestPoints=\"");
      sb.append(tests);
      sb.append("\" failures=\"");
      sb.append(failures);
      sb.append("\"\n");

      fileAppendLock.lock();
      try {

         if (destination.exists()) {
            String allPastResults = "";
            InputStream fileInputStream = new FileInputStream(destination);
            BufferedReader in = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
            String result;
            in.readLine(); // skip the first line
            while (null != (result = in.readLine())) {
               if (result.startsWith("FAIL") || result.startsWith("PASS")) {
                  totalSuites++;
                  allPastResults += result + "\n";
                  if (result.startsWith("FAIL")) {
                     failedSuites++;
                  }
               }
            }
            fileInputStream.close();
            sb.insert(0, allPastResults);
         }
         sb.insert(0, "Results: " + failedSuites + " Failed of " + totalSuites + " Tests\n\n");

         FileOutputStream fileOutputStream = new FileOutputStream(destination);
         fileOutputStream.write(sb.toString().getBytes("UTF-8"));
         fileOutputStream.close();
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         fileAppendLock.unlock();
      }
   }
}

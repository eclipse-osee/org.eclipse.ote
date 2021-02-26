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
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andy Jury
 */
public class JUnitResultFormatWriter implements ITestResultWriter {
   private File destination;

   public JUnitResultFormatWriter(File destination) {
      this.destination = destination;
   }

   @Override
   public void process(String sourceName, List<TestCaseInfo> testCases) {
      OseeLog.log(this.getClass(), Level.ALL, "JUnitResultFormatWriter Processing " + sourceName);

      final StringBuilder sb = new StringBuilder();

      sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      int tests = 0;
      int failures = 0;
      for (TestCaseInfo info : testCases) {
         tests += info.testPointInfo().size();
         failures += info.getFails();
      }
      sb.append("<testsuite name=\"");
      sb.append(sourceName);
      sb.append("\" errors=\"0\" tests=\"");
      sb.append(tests);
      sb.append("\" failures=\"");
      sb.append(failures);
      sb.append("\">\n");
      for (TestCaseInfo testCase : testCases) {
         for (TestPointInfo testPoint : testCase.testPointInfo()) {
            sb.append("<testcase name=\"");
            sb.append(testCase.getName());
            sb.append(testPoint.getNumber());
            sb.append("\" classname=\"");
            sb.append(testCase.getName());
            sb.append("\" time=\"");
            sb.append(testPoint.getTime());
            sb.append("\"");
            if (testPoint.isPass()) {
               sb.append("/>\n");
            } else {
               sb.append(">\n");
               sb.append("<failure>");
               sb.append("Failed on test point " + testCase.getNumber());
               sb.append("</failure>\n");
               sb.append("</testcase>\n");
            }
         }
      }
      sb.append("</testsuite>\n");

      try {
         Lib.writeBytesToFile(sb.toString().getBytes("UTF-8"), destination);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}

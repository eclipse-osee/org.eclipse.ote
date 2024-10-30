/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ote.core.framework.outfile;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.interfaces.ITestPoint;
import org.eclipse.osee.ote.core.framework.outfile.xml.FormalityLevelRecord;
import org.eclipse.osee.ote.core.framework.outfile.xml.TestPointResults;
import org.eclipse.osee.ote.core.framework.outfile.xml.TimeSummary;
import org.eclipse.osee.ote.core.log.record.ScriptResultRecord;
import org.eclipse.osee.ote.core.log.record.TestPointRecord;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.eclipse.osee.ote.core.testPoint.Operation;

/**
 * This class collects info from the outfile records that will be used in calling class to create the GTest.json file.
 *
 * @author Murshed Alam
 */
public class GoogleTestFormatPublisher {
   private Map<String, Object> gTest;
   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
   List<Object> testsuites = new ArrayList<Object>();
   List<Object> testsuite = new ArrayList<Object>();
   List<Object> failedPoints = new ArrayList<Object>();
   private static String testName;
   
   public GoogleTestFormatPublisher(Map<String, Object> gTest) {
      this.gTest = gTest;
   }

   public void publish(LogRecord logRecord) {
      try {
         gTest.put("machineName", InetAddress.getLocalHost().getHostName());
      } catch (UnknownHostException ex) {
         logError(ex, "Unable to determine machine name");
      }
      
      if (logRecord instanceof ScriptResultRecord) {
         ScriptResultRecord srr = (ScriptResultRecord) logRecord;
         testName = logRecord.getMessage();
         gTest.put("name", testName);
         for (XmlizableStream rec : srr.getResults()) {
            Map<String, Object> results = new HashMap<String, Object>();
            if (rec instanceof TimeSummary) {
               TimeSummary ts = (TimeSummary) rec;
               gTest.put("time", ts.getElapsedTime()/1000 + "s");
               gTest.put("timestamp", formatter.format(ts.getStartTime()));
               gTest.put("group", "OTE Group");
               gTest.put("project", "Test Project");
               gTest.put("branch", "dev");
               gTest.put("environment", "LOCAL");
            } else if (rec instanceof TestPointResults) {
               results.put("tests", 1);
               int failures = ((TestPointResults) rec).getFails();
               failures = failures > 0 ? 1: 0;
               results.put("failures", failures);
               results.put("disabled", 0); //not used
               results.put("errors", 0); //not used
               results.put("interactives", ((TestPointResults) rec).getInteractives());
               results.put("aborted", ((TestPointResults) rec).isAborted());
               results.put("name", testName);
               results.put("time", gTest.get("time"));  
               results.put("timestamp", gTest.get("timestamp"));
               Map<String, Object> point = (Map<String, Object>) testsuite.get(0);
               point.put("name", testName);
               point.put("failures", failedPoints);
               results.put("testsuite", testsuite);
               testsuites.add(results);
               gTest.put("tests",  1);
               gTest.put("failures",  failures);
               gTest.put("disabled", 0); //not used
               gTest.put("errors", 0); //not used
            }
         }
         gTest.put("testsuites", testsuites);
      } else if (logRecord instanceof TestPointRecord) {
         ITestPoint testPoint = ((TestPointRecord) logRecord).getTestPoint();
         int number = ((TestPointRecord) logRecord).getNumber();
         TestScript testScript = ((TestPointRecord) logRecord).getSource();
         Instant instant = ((TestPointRecord) logRecord).getInstant();
         boolean overall = testPoint.isPass();      
         handleTestPoint(testPoint, testScript, instant, number, "", overall, String.valueOf(number));
      } else if (logRecord instanceof FormalityLevelRecord) {
         FormalityLevelRecord flr = (FormalityLevelRecord) logRecord;
         gTest.put("buildnumber", flr.getBuildId());
      }
      
   }

   private void handleTestPoint(ITestPoint testPoint, TestScript testScript, Instant instant, int number, String groupName, boolean overallPass, String levelNum) {
      if (testPoint instanceof CheckPoint) {
         Map<String, Object> point =
            convertCheckPoint((CheckPoint) testPoint, testScript, instant, number, groupName, overallPass, levelNum);
         testsuite.clear();
         testsuite.add(point);
      } else if (testPoint instanceof CheckGroup) {
         CheckGroup group = (CheckGroup) testPoint;
         ArrayList<ITestPoint> groupPoints = group.getTestPoints();
         Operation op = group.getOperation();
         String curGroupName = group.getGroupName() + " [" + op.getName() + "]";
            ITestPoint tp = groupPoints.get(0);
            handleTestPoint(tp, testScript, instant, number, curGroupName, overallPass, levelNum);
      }
   }

   private Map<String, Object> convertCheckPoint(CheckPoint checkPoint, TestScript testScript, Instant instant, int number, String groupName, boolean overallPass, String levelNum) {
      Map<String, Object> tpMap = new HashMap<String, Object>();
      tpMap.put("status", "RUN");
      tpMap.put("result", "COMPLETED");
      tpMap.put("isInteractive", checkPoint.isInteractive());
      tpMap.put("classname", testScript.getClass());
      tpMap.put("time", checkPoint.getElpasedTime()/1000 + "s");
      tpMap.put("steps", testScript.getClass().getName());
      tpMap.put("timestamp", formatter.format(Timestamp.from(instant)));
      if (!groupName.isEmpty()) {
         tpMap.put("groupName", groupName);
      }
      if(!overallPass) {
         Map<String, Object> failedPoint = new HashMap<String, Object>();
         failedPoint.put("failure", "Test failure #" + number +  ". Expected: '" + checkPoint.getExpected() + "' Actual: '" +   checkPoint.getActual() +"'");
         failedPoint.put("type",  "");
         failedPoints.add(failedPoint);
      }
      return tpMap;
   }

   private void logError(final Exception ex, final String message) {
      if (message != null && message.trim().length() > 0) {
         System.err.println(message);
      }
      if (ex != null) {
         ex.printStackTrace();
      } else {
         Throwable throwable = new Throwable();
         throwable.printStackTrace();
      }
   }
}
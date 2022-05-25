/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.ote.core.environment.interfaces.ITestPoint;
import org.eclipse.osee.ote.core.framework.outfile.xml.TestPointResults;
import org.eclipse.osee.ote.core.framework.outfile.xml.TimeSummary;
import org.eclipse.osee.ote.core.log.record.ScriptResultRecord;
import org.eclipse.osee.ote.core.log.record.TestPointRecord;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.eclipse.osee.ote.core.testPoint.Operation;

/**
 * This class collects info from the outfile records that will be used in calling class to create the Minimum.json file.
 *
 * @author Andy Jury
 */
public class MinimumPublisher {

   private final Map<String, Object> minimum;

   public MinimumPublisher(Map<String, Object> minimum) {
      this.minimum = minimum;
   }

   void publish(LogRecord logRecord) {
      try {
         minimum.put("machineName", InetAddress.getLocalHost().getHostName());
      } catch (UnknownHostException ex) {
         logError(ex, "Unable to determine machine name");
      }
      if (logRecord instanceof ScriptResultRecord) {
         ScriptResultRecord srr = (ScriptResultRecord) logRecord;
         minimum.put("ScriptName", logRecord.getMessage());
         for (XmlizableStream rec : srr.getResults()) {
            if (rec instanceof TimeSummary) {
               TimeSummary ts = (TimeSummary) rec;
               minimum.put("elapsedTime", ts.getElapsedTime());
               minimum.put("startTime", ts.getStartTime());
               minimum.put("endTime", ts.getEndTime());
            } else if (rec instanceof TestPointResults) {
               Map<String, Object> results = new HashMap<String, Object>();
               results.put("passes", ((TestPointResults) rec).getPasses());
               results.put("fails", ((TestPointResults) rec).getFails());
               results.put("interactives", ((TestPointResults) rec).getInteractives());
               results.put("aborted", ((TestPointResults) rec).isAborted());
               results.put("total", ((TestPointResults) rec).getTotal());
               minimum.put("results", results);
            }
         }
      } else if (logRecord instanceof TestPointRecord) {
         @SuppressWarnings("unchecked")
         List<Object> testPoints = (List<Object>) minimum.get("testPoints");
         if (testPoints == null) {
            testPoints = new ArrayList<Object>();
            minimum.put("testPoints", testPoints);
         }
         ITestPoint testPoint = ((TestPointRecord) logRecord).getTestPoint();
         int number = ((TestPointRecord) logRecord).getNumber();
         boolean overall = testPoint.isPass();
         handleTestPoint(testPoint, testPoints, number, "", overall, String.valueOf(number));
      }
   }

   private void handleTestPoint(ITestPoint testPoint, List<Object> testPoints, int number, String groupName, boolean overallPass, String levelNum) {
      if (testPoint instanceof CheckPoint) {
         Map<String, Object> point =
            convertCheckPoint((CheckPoint) testPoint, number, groupName, overallPass, levelNum);
         testPoints.add(point);
      } else if (testPoint instanceof CheckGroup) {
         CheckGroup group = (CheckGroup) testPoint;
         ArrayList<ITestPoint> groupPoints = group.getTestPoints();
         Operation op = group.getOperation();
         String curGroupName = group.getGroupName() + " [" + op.getName() + "]";
         for (int i = 0; i < groupPoints.size(); i++) {
            ITestPoint tp = groupPoints.get(i);
            handleTestPoint(tp, testPoints, number, curGroupName, overallPass, levelNum + "." + (i + 1));
         }
      }
   }

   private Map<String, Object> convertCheckPoint(CheckPoint checkPoint, int number, String groupName, boolean overallPass, String levelNum) {
      Map<String, Object> tpMap = new HashMap<String, Object>();
      tpMap.put("name", checkPoint.getTestPointName());
      tpMap.put("expected", checkPoint.getExpected());
      tpMap.put("actual", checkPoint.getActual());
      tpMap.put("pass", checkPoint.isPass());
      tpMap.put("isInteractive", checkPoint.isInteractive());
      tpMap.put("number", number);
      tpMap.put("overall", overallPass);
      if (!groupName.isEmpty()) {
         tpMap.put("groupName", groupName);
      }
      tpMap.put("tpLevel", levelNum);
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

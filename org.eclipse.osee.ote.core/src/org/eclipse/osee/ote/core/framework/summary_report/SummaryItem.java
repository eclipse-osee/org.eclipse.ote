/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ote.core.framework.summary_report;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.osee.ote.core.framework.saxparse.elements.TestPointResultsData;

/**
 * @author Dominic Leiner
 */
public class SummaryItem {
   private String testScriptName;
   private TestPointResultsData testPointsResult;
   private List<TestCaseInfo> testCases;
   private HashMap<String, Boolean> requirementStats;

   public SummaryItem() {
      testCases = new LinkedList<TestCaseInfo>();
   }

   public void setTestPointResults(TestPointResultsData data) {
      testPointsResult = data;
   }

   public TestPointResultsData getTestPointsResult() {
      return testPointsResult;
   }

   public void addTestCases(List<TestCaseInfo> testCases) {
      testCases.addAll(testCases);
   }

   public List<TestCaseInfo> getTestCases() {
      return testCases;
   }

   public void setRequirementStats(HashMap<String, Boolean> requirementStats) {
      this.requirementStats = requirementStats;
   }

   public void setName(String name) {
      this.testScriptName = name;
   }

   public String getName() {
      return testScriptName;
   }

   @Override
   public String toString() {
      return "TestCase: " + testScriptName + " TestPointResult: " + testPointsResult;
   }

   public String getMdTableFormat() {
      return getMdTableFormat(false);
   }

   public String getMdTableFormat(boolean includeInteractive) {
      // Format: | Test | Pass | Fail | Total |
      // Format: | Test | Pass | Fail | Interactives | Total |
      try {
         return "|" + testScriptName + "|" + testPointsResult.getPass() + "|" + testPointsResult.getFail() + "|"
               + (includeInteractive && testPointsResult.getInteractive() != null ? (testPointsResult.getInteractive() + "|") : "|")
               + testPointsResult.getTotal() + "|";

      } catch (Exception e) {
         // If a null pointer Happens above, script aborted in a way where the TMO didn't
         // contain Test Points Results.
         return "|" + testScriptName + "| | |" + (includeInteractive ? " |" : "") + " |";
      }
   }

   public String getRequirementTable() {
      String result = "";

      if (testPointsResult == null || testPointsResult.getAborted().equals("true")) {
         result += "\n|" + testScriptName + "|" + "  " + "|" + "FAIL" + "|" + " ABORTED " + "|";
      }

      for (String i : requirementStats.keySet()) {
         result += "\n|" + testScriptName + "|" + i + "|" + (requirementStats.get(i) ? "PASS" : "FAIL") + "|" + " " + "|";
      }

      return result;
   }

   public void setTestCases(List<TestCaseInfo> testCases2) {
      testCases = testCases2;
   }

   public boolean containsAborted() {
      return (testPointsResult == null || testPointsResult.getAborted().equals("true"));
   }

}

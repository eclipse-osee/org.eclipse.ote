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

import java.io.File;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dominic Leiner
 */
public class SummaryReport {
   private static final String REPORT_TITLE = "# Summary Report";

   private static final String ABORT_TABLE_TITLE = "## Test Abort Summary";

   private static final String SUMMARY_TABLE_TITLE = "## Test Result Summary";
   private static final String SUMMARY_TABLE_HEADER = "| Test | Pass | Fail |";
   private static final String SUMMARY_TABLE_FORMAT = "|:----:|-----:|-----:|";

   private static final String TESTPOINT_TABLE_HEADER = "| Test |  TestPoint | Pass/Fail | Comment |";
   private static final String TESTPOINT_TABLE_FORMAT = "|:----:|:----------:|:---------:|---------|";

   private static final String REQ_TABLE_TITLE = "## Test Result Summary by Requirement";
   private static final String REQ_TABLE_HEADER = "| Test |  REQ ID | Pass/Fail | Comment |";
   private static final String REQ_TABLE_FORMAT = "|:----:|:-------:|:---------:|---------|";

   List<SummaryItem> testResultSummary;

   public SummaryReport() {
      testResultSummary = new LinkedList<SummaryItem>();
   }

   public void addAllItems(List<SummaryItem> items) {
      testResultSummary.addAll(items);
   }

   public void addItem(SummaryItem item) {
      testResultSummary.add(item);
   }

   public void setResultSummary(List<SummaryItem> toReturn) {
      this.testResultSummary = toReturn;
   }

   protected List<SummaryItem> getResultSummary() {
      return testResultSummary;
   }
   
   protected int getFileCount() {
      return testResultSummary.size();
   }

   public void importTmoDirectory(File tmoDirectory) {
      addAllItems(SummaryReportGenerator.generate(tmoDirectory).getResultSummary());
   }

   /**
    * Builds a summary report that includes the Test Result Summary, Test Result
    * Summary by Requirements, and Test Abort Summary.
    *
    * @return The summary report as a Markdown-formatted string.
    */
   public String buildSummaryReport() {
      return buildSummaryReport(true, false);
   }

   /**
    * Builds a summary report with the option to exclude the Test Result Summary by
    * Requirements and the option to include Interactives if desired.
    *
    * @param includeRequirementsTable Set to true to include the Requirements
    *                                 Table, or false to exclude it.
    * @param includeInteractives      Set to true to include Interactives, or false
    *                                 to exclude them.
    * @return The summary report as a Markdown-formatted string.
    */
   public String buildSummaryReport(boolean includeRequirementsTable, boolean includeInteractives) {
      if(testResultSummary.size() == 0) {
         return "No test results found. Please ensure that .TMO files are available in the provided directories.";
      }
      
      String result = REPORT_TITLE + "\n";

      List<SummaryItem> abortedList = getAborted();

      if (abortedList.isEmpty()) {
         result += ABORT_TABLE_TITLE + "\n" + "NONE" + "\n" + SUMMARY_TABLE_TITLE + "\n" + buildSummaryTable(includeInteractives)
               + (includeRequirementsTable ? "\n" + REQ_TABLE_TITLE + "\n" + buildRequirementTable() : "");
      } else {
         result += ABORT_TABLE_TITLE + "\n" + buildAbortTable(includeInteractives) + "\n" + SUMMARY_TABLE_TITLE + "\n" + "Aborts Found"
               + (includeRequirementsTable ? "\n" + REQ_TABLE_TITLE + "\n" + "Aborts Found" : "");
      }

      return result;
   }

   private String getTotalsRowFormat(boolean includeInteractives) {
      int totalSum = 0;
      int passSum = 0;
      int failSum = 0;
      int interactiveSum = 0;
      int interactive = 0;

      List<SummaryItem> summaryItemList;
      if (getAborted().isEmpty()) {
         summaryItemList = testResultSummary;
      } else {
         summaryItemList = getAborted();
      }

      for (SummaryItem item : summaryItemList) {
         if (item != null && item.getTestPointsResult() != null) {
            int total = Integer.parseInt(item.getTestPointsResult().getTotal());
            int pass = Integer.parseInt(item.getTestPointsResult().getPass());
            int fail = Integer.parseInt(item.getTestPointsResult().getFail());
            if (includeInteractives && item.getTestPointsResult().getInteractive() != null) {
               interactive = Integer.parseInt(item.getTestPointsResult().getInteractive());
               interactiveSum += interactive;
            }

            totalSum += total;
            passSum += pass;
            failSum += fail;
         }
      }

      return formatTotalsRow(passSum, failSum, interactiveSum, totalSum, includeInteractives);
   }

   private String formatTotalsRow(int passSum, int failSum, int interactiveSum, int totalSum, boolean includeInteractives) {
      DecimalFormat df = new DecimalFormat("#,###");

      return "\n| **Totals:** | **" + df.format(passSum) + "** | **" + df.format(failSum) + "** | **"
            + (includeInteractives ? (df.format(interactiveSum) + "** | **") : "") + df.format(totalSum) + "** |";
   }

   private List<SummaryItem> getAborted() {
      List<SummaryItem> result = new LinkedList<SummaryItem>();
      for (SummaryItem i : testResultSummary) {
         if (i.containsAborted()) {
            result.add(i);
         }
      }
      return result;
   }

   public String buildAbortTable(boolean includeInteractives) {
      String result = SUMMARY_TABLE_HEADER + (includeInteractives ? " Interactive | Total |" : " Total |");
      result += "\n" + SUMMARY_TABLE_FORMAT + (includeInteractives ? "-----:|------:|" : "------:|");

      for (SummaryItem item : testResultSummary) {
         if (item.containsAborted()) {
            result += "\n" + item.getMdTableFormat(includeInteractives);
         }
      }

      result += getTotalsRowFormat(includeInteractives);

      return result;
   }

   public String buildSummaryTable(boolean includeInteractives) {
      String result = SUMMARY_TABLE_HEADER + (includeInteractives ? " Interactive | Total |" : " Total |");
      result += "\n" + SUMMARY_TABLE_FORMAT + (includeInteractives ? "-----:|------:|" : "------:|");

      for (SummaryItem item : testResultSummary) {
         result += "\n" + item.getMdTableFormat(includeInteractives);
      }

      result += getTotalsRowFormat(includeInteractives);

      return result;
   }

   public String buildTestCaseTable() {
      String result = TESTPOINT_TABLE_HEADER;
      result += "\n" + TESTPOINT_TABLE_FORMAT;

      for (SummaryItem item : testResultSummary) {
         for (TestCaseInfo testCase : item.getTestCases()) {
            result += testCase.getMdTableFormat();
         }

      }

      return result;
   }

   public String buildRequirementTable() {
      String result = REQ_TABLE_HEADER;
      result += "\n" + REQ_TABLE_FORMAT;

      for (SummaryItem item : testResultSummary) {
         result += item.getRequirementTable();
      }

      return result;
   }

}

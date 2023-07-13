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
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dominic Leiner
 */
public class SummaryReport {
   private static final String SUMMARY_TABLE_HEADER = "| Test | Total | Pass | Fail | Aborted |";
   private static final String SUMMARY_TABLE_FORMAT = "|:----:|------:|-----:|-----:|--------:|";
   
   private static final String REQ_TABLE_HEADER = "| Test |  REQ? | Pass/Fail | Comment |";
   private static final String REQ_TABLE_FORMAT = "|:----:|:-----:|:---------:|---------|";
   
   
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
   
   public void importTmoDirectory(File tmoDirectory) {
      addAllItems(TmoImporter.importTmoDirectory(tmoDirectory).getResultSummary());
   }
      
   public String buildSummaryTable() {
      String result = SUMMARY_TABLE_HEADER;
      result += "\n" + SUMMARY_TABLE_FORMAT;
      
      for(SummaryItem item : testResultSummary) {
         result += "\n" + item.getMdTableFormat();
      }
      
      return result;
   }
   
   public String buildReqTable() {
      String result = REQ_TABLE_HEADER;
      result += "\n" + REQ_TABLE_FORMAT;
      
      for(SummaryItem item : testResultSummary) {
         for(TestCaseInfo testCase : item.getTestCases()) {
            result += testCase.getMdTableFormat();
         }
         
      }
      
      return result;
   }

}

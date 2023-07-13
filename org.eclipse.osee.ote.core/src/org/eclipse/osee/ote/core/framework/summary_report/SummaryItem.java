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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ote.core.framework.saxparse.elements.TestPointResultsData;

/**
 * @author Dominic Leiner
 */
public class SummaryItem {
   String name;
   TestPointResultsData testPointsResult;
   private List<TestCaseInfo> testCases;
   
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
   
   public void setName(String name) {
      this.name = name;
   }
   
   public String getName() {
      return name;
   }
   
   @Override
   public String toString() {
      return "TestCase: " + name + " TestPointResult: " + testPointsResult;
   }
   
   public String getMdTableFormat() {
      //Format: | Test | Total | Pass | Fail | Aborted |
      try {
         return "|" + name  +"|" + testPointsResult.getTotal() + "|" + testPointsResult.getPass() + "|" + testPointsResult.getFail() + "|" + testPointsResult.getAborted() + "|";        
      } catch (Exception e) {
         //If a null pointer Happens above, script aborted in a way where the TMO didn't contain Test Points Results.
         return "|" + name +  "| | | | true |";
      }
   }

   public void setTestCases(List<TestCaseInfo> testCases2) {
      testCases = testCases2;
   }

}

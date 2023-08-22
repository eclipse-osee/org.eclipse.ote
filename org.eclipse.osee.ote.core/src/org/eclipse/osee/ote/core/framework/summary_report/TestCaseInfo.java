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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andy Jury, Dominic Leiner
 */
public class TestCaseInfo {
   private String name = null;
   private String scriptName = null;
   private String number = null;
   private int fail = 0;
   private int pass = 0;
   private List<TestPointInfo> testPointInfo = new ArrayList<TestPointInfo>();

   public void setScriptName(String scriptname) {
      this.scriptName = scriptname;
   }
   
   public void setName(String string) {
      this.name = string;
   }

   public String getName() {
      return name;
   }

   public void setNumber(String string) {
      this.number = string;
   }

   public String getNumber() {
      return number;
   }

   public void incrementFail() {
      fail++;
   }

   public int getFails() {
      return fail;
   }

   public void incrementPass() {
      pass++;
   }
   
   public int getPass() {
      return pass;
   }
   
   protected void removeTestPoint() {
      testPointInfo.remove(0);
   }

   public void addTestPoint() {
      testPointInfo.add(new TestPointInfo());
   }

   public TestPointInfo getLastTestPoint() {
      return testPointInfo.get(testPointInfo.size() - 1);
   }

   public List<TestPointInfo> testPointInfo() {
      return testPointInfo;
   }
   
   public String getMdTableFormat() {
      // Format: "| Test |  REQ? | Pass/Fail | Comment |";
      String result = "";
      for(TestPointInfo item : testPointInfo) {
         String itemResult = item.isPass() ? "Pass" : "Fail";
         result += "\n" + "|" + scriptName + "|" + item.getName() + "|" + itemResult  +"|" + "  " + "|";
      }
      return result;
   }
   
}

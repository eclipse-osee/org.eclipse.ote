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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andy Jury
 */
public class TestCaseInfo {
   private String name;
   private String number;
   private int fail;
   private List<TestPointInfo> testPointInfo = new ArrayList<TestPointInfo>();

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
      //INTENTIONALLY EMPTY
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
}

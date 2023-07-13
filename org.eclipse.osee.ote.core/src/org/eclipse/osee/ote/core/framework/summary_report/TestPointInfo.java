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

/**
 * @author Andy Jury
 */
public class TestPointInfo {
   private String number = null;
   private String name = null;
   private boolean pass = false;
   private String time = null;

   public void setPass(boolean b) {
      pass = b;
   }

   public boolean isPass() {
      return pass;
   }

   public void setNumber(String string) {
      number = string;
   }

   public String getNumber() {
      return number;
   }

   public void setTime(String string) {
      time = string;
   }

   public String getTime() {
      return time;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}

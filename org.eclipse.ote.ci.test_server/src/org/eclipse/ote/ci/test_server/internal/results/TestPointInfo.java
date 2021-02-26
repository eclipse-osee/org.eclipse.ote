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

/**
 * @author Andy Jury
 */
public class TestPointInfo {
   private String number;
   private boolean pass;
   private String time;

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
}

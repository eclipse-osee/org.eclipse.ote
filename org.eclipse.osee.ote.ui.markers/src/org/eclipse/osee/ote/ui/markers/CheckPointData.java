/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.ui.markers;

/**
 * @author Andrew M. Finkbeiner
 */
public class CheckPointData {

   private boolean isFailed = false;
   private String name;
   private String expected;
   private String actual;

   public boolean isFailed() {
      return isFailed;
   }

   public void setFailed(boolean failed) {
      this.isFailed = failed;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setExpected(String expected) {
      this.expected = expected;
   }

   public void setActual(String actual) {
      this.actual = actual;
   }

   public String getName() {
      return name;
   }

   public String getExpected() {
      return expected;
   }

   public String getActual() {
      return actual;
   }

}

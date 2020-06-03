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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestPointData {

   private boolean isFailed;
   private final List<CheckPointData> data = new ArrayList<>();
   private String number;
   private StackTraceCollection stacktrace;

   public boolean isFailed() {
      return isFailed;
   }

   public void add(CheckPointData checkPoint) {
      data.add(checkPoint);
   }

   public void setFailed(boolean failed) {
      isFailed = failed;
   }

   public void setNumber(String number) {
      this.number = number;
   }

   public String getNumber() {
      return number;
   }

   public void setStackTrace(StackTraceCollection currentStackTrace) {
      this.stacktrace = currentStackTrace;
   }

   public List<CheckPointData> getCheckPointData() {
      return data;
   }

   public StackTraceCollection getStacktraceCollection() {
      return stacktrace;
   }
}

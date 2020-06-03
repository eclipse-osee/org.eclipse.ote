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

package org.eclipse.osee.ote.message.elements;

public class MsgWaitResult {
   private final long time;
   private final int xmitCount;
   private final boolean passed;

   public MsgWaitResult(long time, int xmitCount, boolean passed) {
      this.time = time;
      this.xmitCount = xmitCount;
      this.passed = passed;
   }

   public long getElapsedTime() {
      return time;
   }

   public int getXmitCount() {
      return xmitCount;
   }

   public boolean isPassed() {
      return passed;
   }

}

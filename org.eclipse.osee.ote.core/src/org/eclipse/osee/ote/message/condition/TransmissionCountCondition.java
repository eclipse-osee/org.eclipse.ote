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

package org.eclipse.osee.ote.message.condition;

public class TransmissionCountCondition extends AbstractCondition {

   private final int max;

   public TransmissionCountCondition(int max) {
      this.max = max;
   }

   @Override
   public boolean check() {
      return getCheckCount() >= max;
   }

   public int getMaxTransmitCount() {
      return max;
   }

}

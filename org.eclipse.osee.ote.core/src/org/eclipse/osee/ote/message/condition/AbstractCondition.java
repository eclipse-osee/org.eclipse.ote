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

/**
 * @author Ken J. Aguilar
 */
public abstract class AbstractCondition implements ICondition {

   @Override
   public boolean checkAndIncrement() {
      incrementCheckCount();
      return check();
   }

   private int checkCount = 0;

   protected void incrementCheckCount() {
      checkCount++;
   }

   @Override
   public int getCheckCount() {
      return checkCount;
   }
}

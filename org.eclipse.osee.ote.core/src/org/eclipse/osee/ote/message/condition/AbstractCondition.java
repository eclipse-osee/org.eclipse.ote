/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

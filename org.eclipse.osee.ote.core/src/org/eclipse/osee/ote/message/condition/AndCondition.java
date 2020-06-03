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

import java.util.Collection;

/**
 * Checks that atleast one condition of a series of conditions is true
 * 
 * @author Ken J. Aguilar
 */
public class AndCondition extends AbstractCondition {

   private final ICondition[] conditions;

   public AndCondition(ICondition... conditions) {
      this.conditions = conditions;
   }

   public AndCondition(Collection<ICondition> conditions) {
      this.conditions = conditions.toArray(new ICondition[conditions.size()]);
   }

   @Override
   public boolean check() {
      for (ICondition condition : conditions) {
         if (condition.check()) {
            return true;
         }
      }
      return false;
   }

}

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

import org.eclipse.osee.ote.message.elements.DiscreteElement;

public class StringTrimCondition extends AbstractCondition implements IDiscreteElementCondition<String> {

   private final DiscreteElement<String> element;
   private final String value;
   private String actualValue;

   public StringTrimCondition(DiscreteElement<String> element, String value) {
      this.element = element;
      this.value = value;
   }

   @Override
   public boolean check() {
      actualValue = element.getValue().trim();
      return actualValue.equals(value);
   }

   @Override
   public String getLastCheckValue() {
      return actualValue;
   }
}

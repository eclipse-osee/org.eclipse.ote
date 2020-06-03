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

import org.eclipse.osee.ote.message.elements.StringElement;

/**
 * @author Ken J. Aguilar
 */

public class StringComparisonCondition extends AbstractCondition implements IDiscreteElementCondition<String> {

   private final StringElement element;
   private final String value;
   private final StringOperation operation;
   private String lastValue = null;

   public StringComparisonCondition(StringElement element, StringOperation operation, String value) {
      this.element = element;
      this.operation = operation;
      this.value = value;
   }

   @Override
   public String getLastCheckValue() {
      return lastValue;
   }

   @Override
   public boolean check() {
      lastValue = element.getValue();
      return operation.evaluate(lastValue, value);
   }

   public StringElement getElement() {
      return element;
   }

   public String getValue() {
      return value;
   }

   public StringOperation getOperation() {
      return operation;
   }

}

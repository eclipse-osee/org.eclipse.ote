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

/**
 * @author Ken J. Aguilar
 */

public class ComparisonCondition<T extends Comparable<T>> extends AbstractCondition implements IDiscreteElementCondition<T> {

   private final DiscreteElement<T> element;
   private final T value;
   private final EqualityOperation operation;
   private T lastValue = null;

   public ComparisonCondition(DiscreteElement<T> element, EqualityOperation operation, T value) {
      this.element = element;
      this.operation = operation;
      this.value = value;
   }

   @Override
   public T getLastCheckValue() {
      return lastValue;
   }

   @Override
   public boolean check() {
      lastValue = element.getValue();
      return operation.evaluate(lastValue, value);
   }

   public DiscreteElement<T> getElement() {
      return element;
   }

   public T getValue() {
      return value;
   }

   public EqualityOperation getOperation() {
      return operation;
   }

}

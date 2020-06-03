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

public class TransitionCondition<T extends Comparable<T>> extends AbstractCondition implements IDiscreteElementCondition<T> {

   private final DiscreteElement<T> element;
   private final T transitionFromValue;
   private final T transitionToValue;
   private T lastValue = null;

   public TransitionCondition(DiscreteElement<T> element, T transitionFromValue, T transitionToValue) {
      this.element = element;
      this.transitionToValue = transitionToValue;
      this.transitionFromValue = transitionFromValue;
   }

   @Override
   public T getLastCheckValue() {
      return lastValue;
   }

   @Override
   public boolean check() {
      T currentValue = element.getValue();
      if (lastValue == null) {
         lastValue = currentValue;
         return false;
      }
      boolean result = transitionFromValue.equals(lastValue) && transitionToValue.equals(currentValue);
      lastValue = currentValue;
      return result;
   }

   public DiscreteElement<T> getElement() {
      return element;
   }

   public T getTransitionFromValue() {
      return transitionFromValue;
   }

   public T getTransitionToValue() {
      return transitionToValue;
   }

}

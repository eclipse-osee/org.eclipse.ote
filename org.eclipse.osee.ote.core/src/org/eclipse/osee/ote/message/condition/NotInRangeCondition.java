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

import org.eclipse.osee.ote.message.elements.DiscreteElement;

public class NotInRangeCondition<T extends Comparable<T>> extends AbstractCondition implements IDiscreteElementCondition<T> {

   private final DiscreteElement<T> element;
   private final T minValue;
   private final T maxValue;
   private final boolean minInclusive;
   private final boolean maxInclusive;
   private T actualValue;

   public NotInRangeCondition(DiscreteElement<T> element, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive) {
      this.element = element;
      this.minValue = element.elementMask(minValue);
      this.maxValue = element.elementMask(maxValue);
      this.maxInclusive = maxInclusive;
      this.minInclusive = minInclusive;
   }

   @Override
   public boolean check() {
      actualValue = element.getValue();
      boolean result = minInclusive ? actualValue.compareTo(minValue) < 0 : actualValue.compareTo(minValue) <= 0;
      return result | (maxInclusive ? actualValue.compareTo(maxValue) > 0 : actualValue.compareTo(maxValue) >= 0);
   }

   @Override
   public T getLastCheckValue() {
      return actualValue;
   }
}

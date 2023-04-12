/*********************************************************************
 * Copyright (c) 2023 Boeing
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
 * @author Shandeep Singh
 */
public class BitEqualsCondition<T extends Comparable<T>> extends AbstractCondition implements IDiscreteElementCondition<T> {

   private final DiscreteElement<T> element;
   private final T value;
   private final boolean notEquals;
   private T actualValue;
   private int msb;
   private int lsb;

   public BitEqualsCondition(DiscreteElement<T> element, int msb, int lsb, T value) {
      this(element, false, msb, lsb, value);
   }

   /**
    * sets up a condition that only passes when the notEquals flag is set to false and actual value equals the expected
    * value or when the notEquals flag is true and the actual value does not equal the expected.
    */
   public BitEqualsCondition(DiscreteElement<T> element, boolean notEquals, int msb, int lsb, T value) {
      this.element = element;
      this.value = element.elementMask(value);
      this.notEquals = notEquals;
      this.msb = msb;
      this.lsb = lsb;
   }

   @Override
   public boolean check() {
      actualValue = element.getBitValue(msb, lsb);
      return actualValue.equals(value) ^ notEquals;
   }

   @Override
   public T getLastCheckValue() {
      return actualValue;
   }
}

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

import org.eclipse.osee.ote.message.Message;

/**
 * This condition is for checking bit equals condition at the Message level
 * 
 * @author Shandeep Singh
 */
public class MessageBitEqualsCondition<T extends Comparable<T>> extends AbstractCondition implements IDiscreteElementCondition<T> {

   private final Message message;
   private final Long value;
   private final boolean notEquals;
   private Long actualValue;
   private int byteOffset;
   private int msb;
   private int lsb;

   public MessageBitEqualsCondition(Message message, int byteOffset, int msb, int lsb, Long value) {
      this(message, false, byteOffset, msb, lsb, value);
   }

   /**
    * sets up a condition that only passes when the notEquals flag is set to false and actual value equals the expected
    * value or when the notEquals flag is true and the actual value does not equal the expected.
    */
   public MessageBitEqualsCondition(Message message, boolean notEquals, int byteOffset, int msb, int lsb, Long value) {
      this.message = message;
      this.value = value;
      this.notEquals = notEquals;
      this.byteOffset = byteOffset;
      this.msb = msb;
      this.lsb = lsb;
   }

   @Override
   public boolean check() {
      actualValue = message.getMemoryResource().getMem().getLong(byteOffset, msb, lsb);
      return actualValue.equals(value) ^ notEquals;
   }

   @Override
   public T getLastCheckValue() {
      return (T) actualValue;
   }
}

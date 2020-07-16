/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ote.message.elements;

import java.util.Collection;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingSignedInteger8Element;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * Accommodates all 2's compliment integral elements up to 8 bits in length
 * @author Michael P. Masterson
 */
public class SignedInteger8Element extends NumericElement<Byte> {

   public SignedInteger8Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   @Override
   public SignedInteger8Element findElementInMessages(Collection<? extends Message> messages) {
      return (SignedInteger8Element) super.findElementInMessages(messages);
   }
   
   @Override
   public SignedInteger8Element switchMessages(Collection<? extends Message> messages) {
      return (SignedInteger8Element) super.switchMessages(messages);
   }

   @Override
   public String toString(Byte obj) {
      return obj + "(0x" + Integer.toHexString(obj).toUpperCase() + ")";
   }

   @Override
   public void setValue(Byte value) {
      getMsgData().getMem().setInt(value, byteOffset, msb, lsb);
   }

   @Override
   public Byte getValue() {
      return Byte.valueOf((byte)getMsgData().getMem().getSignedInt(byteOffset, msb, lsb));
   }

   @Override
   public Byte valueOf(MemoryResource mem) {
      return Byte.valueOf((byte)mem.getSignedInt(byteOffset, msb, lsb));
   }

   /**
    * This function will verify that this signal is pulsed for 2 cycles.
    * @param accessor 
    * @param value The value to be checked
    * @throws InterruptedException 
    */
   public void checkPulse(ITestAccessor accessor, byte value) throws InterruptedException {

      byte nonPulsedValue = 0;
      if (value == 0) {
         nonPulsedValue = 1;
      }

      checkPulse(accessor, value, nonPulsedValue);

   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      this.set(accessor, Byte.parseByte(value));
   }

   @Override
   public Byte elementMask(Byte value) {
      return value;
   }

   @Override
   public long getNumericBitValue() {
      return getValue() & 0xFFFFL;
   }

   @Override
   protected DiscreteElement<Byte> getNonMappingElement() {
      return new NonMappingSignedInteger8Element(this);
   }

}

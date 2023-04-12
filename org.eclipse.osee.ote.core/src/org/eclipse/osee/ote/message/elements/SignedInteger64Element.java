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
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingSignedInteger64Element;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 * @author Michael P. Masterson
 */
public class SignedInteger64Element extends NumericElement<Long> {

   public SignedInteger64Element(Message message, String elementName, MessageData messageData,
         int byteOffset, int msb, int lsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb);
   }

   public SignedInteger64Element findElementInMessages(Collection<? extends Message> messages) {
      return (SignedInteger64Element) super.findElementInMessages(messages);
   }

   @Override
   public SignedInteger64Element switchMessages(Collection<? extends Message> messages) {
      return (SignedInteger64Element) super.switchMessages(messages);
   }

   @Override
   public String toString(Long obj) {
      long value = elementMask(obj);
      return value + "(0x" + Long.toHexString(value).toUpperCase() + ")";
   }

   @Override
   public void setValue(Long value) {
      getMsgData().getMem().setLong(value, byteOffset, msb, lsb);
   }

   @Override
   public Long getValue() {
      return Long.valueOf(getMsgData().getMem().getSignedLong(byteOffset, msb, lsb));
   }
   
   @Override
   public Long getBitValue(int msb, int lsb) {
      return Long.valueOf(getMsgData().getMem().getSignedLong(byteOffset, msb, lsb));
   }

   @Override
   public Long valueOf(MemoryResource mem) {
      return Long.valueOf(mem.getSignedLong(byteOffset, msb, lsb));
   }

   /**
    * Verifies that the element is set to "value" within the number of "milliseconds" passed.
    * @param accessor 
    * @param value Expected value.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    * @throws InterruptedException 
    */
   public boolean check(ITestAccessor accessor, long value,
         int milliseconds) throws InterruptedException {
      return super.check(accessor, (CheckGroup) null, removeSign(value), milliseconds);
   }

   /**
    * This function will verify that this signal is pulsed for 2 cycles.
    * @param accessor 
    * @param value The value to be checked
    * @throws InterruptedException 
    */
   public void checkPulse(ITestAccessor accessor, long value) throws InterruptedException {
      long nonPulsedValue = 0;
      if (value == 0) {
         nonPulsedValue = 1;
      }

      checkPulse(accessor, value, nonPulsedValue);

   }

   /**
    * Sets the element to the "value" passed.
    * @param accessor 
    * @param value The value to set.
    */
   public void set(ITestEnvironmentAccessor accessor, long value) {
      super.set(accessor, value);
   }

   /**
    * Sets the element to the "value" passed.
    * 
    * @param value The value to set.
    */
   @Deprecated
   public void set(long value) {
      setValue(value);
   }

   /**
    * Sets the element to the "value" passed and immediately sends the message that contains it..
    * @param accessor 
    * @param value The value to set.
    */
   public void setAndSend(ITestEnvironmentAccessor accessor, long value) {
      this.set(accessor, value);
      super.sendMessage();
   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor,
         String value) throws IllegalArgumentException {
      this.set(accessor, Long.parseLong(value));
   }

   @Override
   protected DiscreteElement<Long> getNonMappingElement() {
      return new NonMappingSignedInteger64Element(this);
   }

   @Override
   public Long elementMask(Long value) {
      return removeSign(value);
   }

   @Override
   public long getNumericBitValue() {
      return getValue();
   }

}
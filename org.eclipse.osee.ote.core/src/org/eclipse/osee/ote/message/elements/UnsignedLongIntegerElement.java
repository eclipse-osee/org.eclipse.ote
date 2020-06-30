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
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * Provides unsigned long integer element capabilities from 1 to 63 bits in length
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 * @author Michael P. Masterson
 */
public class UnsignedLongIntegerElement extends NumericElement<Long> {

   public UnsignedLongIntegerElement(Message message, String elementName, MessageData messageData,
         int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public UnsignedLongIntegerElement(Message message, String elementName, MessageData messageData,
         int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   @Override
   public UnsignedLongIntegerElement findElementInMessages(Collection<? extends Message> messages) {
      return (UnsignedLongIntegerElement) super.findElementInMessages(messages);
   }

   @Override
   public UnsignedLongIntegerElement switchMessages(Collection<? extends Message> messages) {
      return (UnsignedLongIntegerElement) super.switchMessages(messages);
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
      return Long.valueOf(getMsgData().getMem().getLong(byteOffset, msb, lsb));
   }

   @Override
   public Long valueOf(MemoryResource mem) {
      return Long.valueOf(mem.getLong(byteOffset, msb, lsb));
   }

   /**
    * Checks that this element correctly forwards a message sent from cause with the value passed.
    * @param accessor 
    * @param cause The originator of the signal
    * @param value The value sent by cause and being forwarded by this element
    * @throws InterruptedException 
    */
   public void checkForwarding(ITestAccessor accessor, UnsignedLongIntegerElement cause,
         long value) throws InterruptedException {
      value = removeSign(value);
      /* check for 0 to begine */
      check(accessor, 0, 0);

      /* Set the DP1 Mux Signal */
      cause.set(accessor, value);

      /* Chk Value on DP2 */
      check(accessor, value, 1000);

      /* Set DP1 to 0 */
      cause.set(accessor, 0);

      /* Init DP2 Mux to 0 */
      set(accessor, 0);

      /* Chk Value on DP2 is still set */
      check(accessor, value, 500);

      /* Chk DP2 is 0 for two-pulse signals and high for four-pulse signal */
      check(accessor, 0, 500);

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
   protected Element getNonMappingElement() {
      return this;
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
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
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingSignedIntegerElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Michael P. Masterson
 */
public class SignedIntegerElement extends NumericElement<Integer> {

   public SignedIntegerElement(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public SignedIntegerElement(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   @Override
   public SignedIntegerElement findElementInMessages(Collection<? extends Message> messages) {
      return (SignedIntegerElement) super.findElementInMessages(messages);
   }
   
   @Override
   public SignedIntegerElement switchMessages(Collection<? extends Message> messages) {
      return (SignedIntegerElement) super.switchMessages(messages);
   }

   @Override
   public String toString(Integer obj) {
      return obj + "(0x" + Integer.toHexString(obj).toUpperCase() + ")";
   }

   @Override
   public void setValue(Integer value) {
      getMsgData().getMem().setInt(value, byteOffset, msb, lsb);
   }

   @Override
   public Integer getValue() {
      return Integer.valueOf(getMsgData().getMem().getSignedInt(byteOffset, msb, lsb));
   }

   @Override
   public Integer valueOf(MemoryResource mem) {
      return Integer.valueOf(mem.getSignedInt(byteOffset, msb, lsb));
   }

   /**
    * Checks that this element correctly forwards a message sent from cause with the value passed.
    * @param accessor 
    * @param cause The originator of the signal
    * @param value The value sent by cause and being forwarded by this element
    * @throws InterruptedException 
    */
   public void checkForwarding(ITestAccessor accessor, SignedIntegerElement cause, int value) throws InterruptedException {
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
    * This function will verify that this signal is pulsed for 2 cycles.
    * @param accessor 
    * @param value The value to be checked
    * @throws InterruptedException 
    */
   public void checkPulse(ITestAccessor accessor, int value) throws InterruptedException {

      int nonPulsedValue = 0;
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
   public void set(ITestEnvironmentAccessor accessor, int value) {
      super.set(accessor, value);
   }

   /**
    * Sets the element to the "value" passed and immediately sends the message that contains it..
    * @param accessor 
    * @param value The value to set.
    */
   public void setAndSend(ITestEnvironmentAccessor accessor, int value) {
      this.set(accessor, value);
      super.sendMessage();
   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      this.set(accessor, Integer.parseInt(value));
   }

   @Override
   public Integer elementMask(Integer value) {
      return value;
   }

   @Override
   public long getNumericBitValue() {
      return getValue() & 0xFFFFFFFFL;
   }

   @Override
   protected SignedIntegerElement getNonMappingElement() {
      return new NonMappingSignedIntegerElement(this);
   }

}
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
package org.eclipse.osee.ote.message.elements;

import java.util.Collection;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingSignedInteger16Element;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class SignedInteger16Element extends NumericElement<Integer> {

   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asSignedInteger16Element(this);
   }

   public SignedInteger16Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public SignedInteger16Element(Message message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
   }

   public SignedInteger16Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   @Override
   public SignedInteger16Element findElementInMessages(Collection<? extends Message> messages) {
      return (SignedInteger16Element) super.findElementInMessages(messages);
   }
   
   @Override
   public SignedInteger16Element switchMessages(Collection<? extends Message<?,?,?>> messages) {
      return (SignedInteger16Element) super.switchMessages(messages);
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
      return Integer.valueOf(getMsgData().getMem().getSignedInt16(byteOffset, msb, lsb));
   }

   @Override
   public Integer valueOf(MemoryResource mem) {
      return Integer.valueOf(mem.getSignedInt16(byteOffset, msb, lsb));
   }

   /**
    * Checks that this element correctly forwards a message sent from cause with the value passed.
    * 
    * @param cause The originator of the signal
    * @param value The value sent by cause and being forwarded by this element
    */
   public void checkForwarding(ITestAccessor accessor, SignedInteger16Element cause, int value) throws InterruptedException {
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
    * 
    * @param value The value to be checked
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
    * 
    * @param value The value to set.
    */
   public void set(ITestEnvironmentAccessor accessor, int value) {
      super.set(accessor, value);
   }

   /**
    * Sets the element to the "value" passed and immediately sends the message that contains it..
    * 
    * @param value The value to set.
    */
   public void setAndSend(ITestEnvironmentAccessor accessor, int value) {
      this.set(accessor, value);
      super.sendMessage();
   }

   /**
    * Waits until the element has a value within the range specified. Either end of the range can be inclusive or not.
    * 
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    * to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    * to the range value.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    */
   public int waitForRange(ITestEnvironmentAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return super.waitForRange(accessor, Integer.valueOf(minValue), minInclusive, Integer.valueOf(maxValue),
         maxInclusive, milliseconds);
   }

   /**
    * Waits until the element has a value within the range specified. Range is assumes to be inclusive.
    * 
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    */
   public int waitForNotRange(ITestEnvironmentAccessor accessor, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      return this.waitForNotRange(accessor, minValue, true, maxValue, true, milliseconds);
   }

   /**
    * Waits until the element has a value within the range specified. Either end of the range can be inclusive or not.
    * 
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    * to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    * to the range value.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    */
   public int waitForNotRange(ITestEnvironmentAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return super.waitForNotRange(accessor, Integer.valueOf(minValue), minInclusive, Integer.valueOf(maxValue),
         maxInclusive, milliseconds);
   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      this.set(accessor, Integer.parseInt(value));
   }

   @Override
   protected Element getNonMappingElement() {
      return new NonMappingSignedInteger16Element(this);
   }

   @Override
   public Integer elementMask(Integer value) {
      return value;
   }

   @Override
   public long getNumericBitValue() {
      return getValue() & 0xFFFFFFFFL;
   }

}
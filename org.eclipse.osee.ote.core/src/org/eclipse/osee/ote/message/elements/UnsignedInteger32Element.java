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
import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingUnsignedInteger32Element;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * Represent any message field as an unsigned integer from 1 to 31 bits in length
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 * @author Michael P. Masterson
 */
public class UnsignedInteger32Element extends NumericElement<Integer> {

   public UnsignedInteger32Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public UnsignedInteger32Element(Message message, String elementName, MessageData messageData,
         int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
   }

   public UnsignedInteger32Element(Message message, String elementName, MessageData messageData,
         int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalMsb, originalLsb);
   }

   @Override
   public UnsignedInteger32Element findElementInMessages(Collection<? extends Message> messages) {
      return (UnsignedInteger32Element) super.findElementInMessages(messages);
   }
   
   @Override
   public UnsignedInteger32Element switchMessages(Collection<? extends Message> messages) {
      return (UnsignedInteger32Element) super.switchMessages(messages);
   }

   @Override
   public String toString(Integer obj) {
      int value = elementMask(obj);
      return value + "(0x" + Integer.toHexString(value).toUpperCase() + ")";
   }

   @Override
   public void setValue(Integer value) {
      getMsgData().getMem().setInt(value, byteOffset, msb, lsb);
   }

   @Override
   public Integer getValue() {
      return Integer.valueOf(getMsgData().getMem().getInt(byteOffset, msb, lsb));
   }

   @Override
   public Integer valueOf(MemoryResource mem) {
      return Integer.valueOf(mem.getInt(byteOffset, msb, lsb));
   }

   /**
    * This function will verify that this signal is pulsed for 2 cycles.
    * @param accessor 
    * @param value The value to be checked
    * @throws InterruptedException 
    */
   public void checkPulse(ITestAccessor accessor, Integer value) throws InterruptedException {

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
   @Override
   public void set(ITestEnvironmentAccessor accessor, Integer value) {
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

   /**
    * Waits until the element has a value within the range specified. Assumes the range is inclusive.
    * @param accessor 
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException 
    */
   public int waitForRange(ITestEnvironmentAccessor accessor, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      return this.waitForRange(accessor, minValue, true, maxValue, true, milliseconds);
   }

   /**
    * Waits until the element has a value within the range specified. Either end of the range can be inclusive or not.
    * @param accessor 
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    * to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    * to the range value.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException 
    */
   public int waitForRange(ITestEnvironmentAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return super.waitForRange(accessor, Integer.valueOf(minValue), minInclusive, Integer.valueOf(maxValue),
         maxInclusive, milliseconds);
   }

   /**
    * Waits until the element has a value within the range specified. Range is assumes to be inclusive.
    * @param accessor 
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException 
    */
   public int waitForNotRange(ITestEnvironmentAccessor accessor, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      return this.waitForNotRange(accessor, minValue, true, maxValue, true, milliseconds);
   }

   /**
    * Waits until the element has a value within the range specified. Either end of the range can be inclusive or not.
    * @param accessor 
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    * to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    * to the range value.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException 
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
      return new NonMappingUnsignedInteger32Element(this);
   }

   @Override
   public Integer elementMask(Integer value) {
      return removeSign(value);
   }

   public static void main(String[] args) {
      final HeaderData hd = new HeaderData("test_data", new MemoryResource(new byte[64], 2, 64));
      UnsignedInteger32Element e1 = new UnsignedInteger32Element(null, "e1", hd, 0, 0, 31);
      UnsignedInteger32Element e2 = new UnsignedInteger32Element(null, "e2", hd, 4, 0, 31);
      e1.setValue(-1);
      e2.setValue(-1000);

      System.out.printf(" e1 value = %d\n", e1.getValue());
      System.out.printf(" e2 value = %d\n", e2.getValue());

   }

   @Override
   public long getNumericBitValue() {
      return getValue() & 0xFFFFFFFFL;
   }

   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asUnsignedIntegerElement(this);
   }

}
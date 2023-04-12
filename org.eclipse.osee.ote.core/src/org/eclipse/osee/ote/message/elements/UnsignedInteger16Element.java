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
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingUnsignedInteger16Element;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Michael P. Masterson
 */
public class UnsignedInteger16Element extends NumericElement<Short> {

   public UnsignedInteger16Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   @Override
   public UnsignedInteger16Element findElementInMessages(Collection<? extends Message> messages) {
      return (UnsignedInteger16Element) super.findElementInMessages(messages);
   }
   
   @Override
   public UnsignedInteger16Element switchMessages(Collection<? extends Message> messages) {
      return (UnsignedInteger16Element) super.switchMessages(messages);
   }

   @Override
   public String toString(Short obj) {
      int value = elementMask(obj);
      return value + "(0x" + Integer.toHexString(value).toUpperCase() + ")";
   }

   @Override
   public void setValue(Short value) {
      getMsgData().getMem().setInt(value, byteOffset, msb, lsb);
   }

   @Override
   public Short getValue() {
      return Short.valueOf((short) getMsgData().getMem().getInt(byteOffset, msb, lsb));
   }

   @Override
   public Short getBitValue(int msb, int lsb) {
      return Short.valueOf((short) getMsgData().getMem().getInt(byteOffset, msb, lsb));
   }
   
   @Override
   public Short valueOf(MemoryResource mem) {
      return Short.valueOf((short) mem.getInt(byteOffset, msb, lsb));
   }

   /**
    * This function will verify that this signal is pulsed for 2 cycles.
    * @param accessor 
    * @param value The value to be checked
    * @throws InterruptedException 
    */
   public void checkPulse(ITestAccessor accessor, Short value) throws InterruptedException {

      short nonPulsedValue = 0;
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
   public void set(ITestEnvironmentAccessor accessor, Short value) {
      super.set(accessor, value);
   }

   /**
    * Sets the element to the "value" passed and immediately sends the message that contains it..
    * @param accessor 
    * @param value The value to set.
    */
   public void setAndSend(ITestEnvironmentAccessor accessor, short value) {
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
   public int waitForRange(ITestEnvironmentAccessor accessor, short minValue, short maxValue, int milliseconds) throws InterruptedException {
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
   public int waitForRange(ITestEnvironmentAccessor accessor, short minValue, boolean minInclusive, short maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return super.waitForRange(accessor, Short.valueOf(minValue), minInclusive, Short.valueOf(maxValue),
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
   public int waitForNotRange(ITestEnvironmentAccessor accessor, short minValue, short maxValue, int milliseconds) throws InterruptedException {
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
   public int waitForNotRange(ITestEnvironmentAccessor accessor, short minValue, boolean minInclusive, short maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return super.waitForNotRange(accessor, Short.valueOf(minValue), minInclusive, Short.valueOf(maxValue),
         maxInclusive, milliseconds);
   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      this.set(accessor, Short.parseShort(value));
   }

   @Override
   protected NumericElement<Short> getNonMappingElement() {
      return new NonMappingUnsignedInteger16Element(this);
   }

   @Override
   public Short elementMask(Short value) {
      return (short) removeSign(value);
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

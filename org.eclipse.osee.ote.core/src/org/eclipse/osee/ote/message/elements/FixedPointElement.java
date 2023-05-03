/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingFixedPointElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Andrew M. Finkbeiner
 * @author John Butler
 */
public class FixedPointElement extends RealElement {

   private final double resolution;
   private final boolean signed;
   private final double offset;

   public FixedPointElement(Message message, String elementName, MessageData messageData, double resolution, boolean signed, int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, resolution, 0, signed, byteOffset, msb, lsb, msb, lsb);
   }

   public FixedPointElement(Message message, String elementName, MessageData messageData, double resolution, double offset, boolean signed, int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, resolution, offset, signed, byteOffset, msb, lsb, msb, lsb);
   }

   public FixedPointElement(Message message, String elementName, MessageData messageData, double resolution, boolean signed, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      this(message, elementName, messageData, resolution, 0, signed, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   public FixedPointElement(Message message, String elementName, MessageData messageData, double resolution, double offset, boolean signed, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
      this.resolution = resolution;
      this.signed = signed;
      this.offset = offset;
   }

   public FixedPointElement(Message message, String elementName, MessageData messageData, double resolution, double offset, boolean signed, int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
      this.resolution = resolution;
      this.signed = signed;
      this.offset = offset;
   }

   /**
    * Sets the element to the "value" passed.
    * 
    * @param value The value to set.
    */
   @Override
   public void set(ITestEnvironmentAccessor accessor, double value) {
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(), new MethodFormatter().add(value),
            this.getMessage());
      }
      setValue(value);

      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
   }

   /**
    * Sets the element to the "value" passeda nd immediately sends the message that contains it...
    * 
    * @param value The value to set.
    */
   @Override
   public void setAndSend(ITestEnvironmentAccessor accessor, double value) {
      this.set(accessor, value);
      super.sendMessage();
   }

   public void setNoLog(ITestEnvironmentAccessor accessor, double value) {
      setValue(value);
   }

   private double toFixed(long value, boolean signed, double resolution, double offset, int msb, int lsb) {
      int shift = 64 - (lsb - msb + 1);
      if (signed) {// two's compliment
         if (value >>> lsb - msb == 1) {// we've got a negative
            value--;
            value = ~value;
            value = value << shift;
            value = value >>> shift;
            value = value * -1;
         }
      } else {
         value = value << shift;
         value = value >>> shift;
      }
      return value * resolution + offset;
   }

   private long toFixedLong(double value, boolean signed, double resolution, double offset, int msb, int lsb) {
      long returnValue = Math.round((value - offset) / resolution);

      if (value > 0 && toFixed(returnValue, signed, resolution, offset, msb, lsb) < 0) {
         returnValue = Math.round((value - resolution - offset) / resolution);
         if (value > 0 && toFixed(returnValue, signed, resolution, offset, msb, lsb) < 0) {
            returnValue = Math.round((value - resolution * 2 - offset) / resolution);
            if (value > 0 && toFixed(returnValue, signed, resolution, offset, msb, lsb) < 0) {
               OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO, getName());
            } else {
               OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO, getName());
            }
         } else {
            OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO, getName());
         }
      }

      if (signed && value < 0) {
         returnValue = returnValue * -1;
         int shift = 64 - (lsb - msb + 1);
         returnValue = returnValue << shift;
         returnValue = ~returnValue;
         returnValue = returnValue >>> shift;
         returnValue++;
      }
      return returnValue;
   }

   @Override
   public void setValue(Double obj) {
      getMsgData().getMem().setLong(toFixedLong(obj, signed, resolution, offset, msb, lsb), byteOffset, msb, lsb);
   }

   @Override
   public void setHex(long hex) {
      getMsgData().getMem().setLong(hex, byteOffset, msb, lsb);
   }

   @Override
   public Double getValue() {
      return toFixed(getRaw(), signed, resolution, offset, msb, lsb);
   }
   
   @Override
   public Double getBitValue(int msb, int lsb) {
      return toFixed(getRaw(), signed, resolution, offset, msb, lsb);
   }

   @Override
   public Double valueOf(MemoryResource mem) {
      return toFixed(getRaw(mem), signed, resolution, offset, msb, lsb);
   }

   @Override
   protected double toDouble(long value) {
      return toFixed(value, signed, resolution, offset, msb, lsb);
   }

   @Override
   protected long toLong(double value) {
      return toFixedLong(value, signed, resolution, offset, msb, lsb);
   }

   /**
    * @return Returns the offset.
    */
   public double getMinVal() {
      return offset;
   }

   /**
    * @return Returns the resolution.
    */
   public double getResolution() {
      return resolution;
   }

   /**
    * @return Returns the signed.
    */
   public boolean isSigned() {
      return signed;
   }

   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asFixedPointElement(this);
   }

   @Override
   protected NonMappingFixedPointElement getNonMappingElement() {
      return new NonMappingFixedPointElement(this);
   }

   @Override
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, Double value, int milliseconds) throws InterruptedException {
      return super.check(accessor, checkGroup, adjust(value), milliseconds);
   }

   @Override
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, Double value) {
      return super.check(accessor, checkGroup, adjust(value));
   }

   @Override
   public boolean checkList(ITestAccessor accessor, CheckGroup checkGroup, boolean isInList, Double[] list, int milliseconds) throws InterruptedException {
      return super.checkList(accessor, checkGroup, isInList, adjust(list), milliseconds);
   }

   @Override
   public boolean checkList(ITestAccessor accessor, CheckGroup checkGroup, boolean wantInList, Double[] list) {
      return super.checkList(accessor, checkGroup, wantInList, adjust(list));
   }

   @Override
   public Double checkMaintain(ITestAccessor accessor, CheckGroup checkGroup, Double value, int milliseconds) throws InterruptedException {
      return super.checkMaintain(accessor, checkGroup, adjust(value), milliseconds);
   }

   @Override
   public Double checkMaintainList(ITestAccessor accessor, CheckGroup checkGroup, Double[] list, boolean isInList, int milliseconds) throws InterruptedException {
      return super.checkMaintainList(accessor, checkGroup, adjust(list), isInList, milliseconds);
   }

   @Override
   public Double checkMaintainNot(ITestAccessor accessor, CheckGroup checkGroup, Double value, int milliseconds) throws InterruptedException {
      return super.checkMaintainNot(accessor, checkGroup, adjust(value), milliseconds);
   }

   @Override
   public Double checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, Double minValue, boolean minInclusive, Double maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return super.checkMaintainNotRange(accessor, checkGroup, minValue, minInclusive, maxValue, maxInclusive,
         milliseconds);
   }

   @Override
   public Double checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, Double minValue, boolean minInclusive, Double maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return super.checkMaintainRange(accessor, checkGroup, minValue, minInclusive, maxValue, maxInclusive,
         milliseconds);
   }

   @Override
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, Double value, int milliseconds) throws InterruptedException {
      return super.checkNot(accessor, checkGroup, adjust(value), milliseconds);
   }

   @Override
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, Double value) {
      return super.checkNot(accessor, checkGroup, adjust(value));
   }

   @Override
   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, Double minValue, boolean minInclusive, Double maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return super.checkNotRange(accessor, checkGroup, minValue, minInclusive, maxValue, maxInclusive, milliseconds);
   }

   @Override
   public boolean checkPulse(ITestAccessor accessor, CheckGroup checkGroup, Double pulsedValue, Double nonPulsedValue, int milliseconds) throws InterruptedException {
      return super.checkPulse(accessor, checkGroup, adjust(pulsedValue), adjust(nonPulsedValue), milliseconds);
   }

   @Override
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, Double minValue, boolean minInclusive, Double maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return super.checkRange(accessor, checkGroup, minValue, minInclusive, maxValue, maxInclusive, milliseconds);
   }

   @Override
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, Double minValue, boolean minInclusive, Double maxValue, boolean maxInclusive) {
      return super.checkRange(accessor, checkGroup, minValue, minInclusive, maxValue, maxInclusive);
   }

   @Override
   public Double waitForList(ITestAccessor accessor, Double[] list, boolean isInList, int milliseconds) throws InterruptedException {
      return super.waitForList(accessor, adjust(list), isInList, milliseconds);
   }

   @Override
   public Double waitForNotRange(ITestEnvironmentAccessor accessor, Double minValue, boolean minInclusive, Double maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return super.waitForNotRange(accessor, minValue, minInclusive, maxValue, maxInclusive, milliseconds);
   }

   @Override
   public Double waitForNotValue(ITestEnvironmentAccessor accessor, Double value, int milliseconds) throws InterruptedException {
      return super.waitForNotValue(accessor, adjust(value), milliseconds);
   }

   @Override
   public Double waitForRange(ITestEnvironmentAccessor accessor, Double minValue, boolean minInclusive, Double maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return super.waitForRange(accessor, minValue, minInclusive, maxValue, maxInclusive, milliseconds);
   }

   @Override
   public Double waitForValue(ITestEnvironmentAccessor accessor, Double value, int milliseconds) throws InterruptedException {
      return super.waitForValue(accessor, adjust(value), milliseconds);
   }

   private Double adjust(Double value) {
      return toDouble(toLong(value));
   }

   private Double[] adjust(Double[] list) {
      Double[] newList = new Double[list.length];
      for (int i = 0; i < list.length; i++) {
         newList[i] = adjust(list[i]);
      }
      return newList;
   }
}

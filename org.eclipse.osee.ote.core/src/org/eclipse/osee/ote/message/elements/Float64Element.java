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

import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingFloat64Element;

/**
 * @author Andrew M. Finkbeiner
 */
public class Float64Element extends RealElement {

   public Float64Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public Float64Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   public Float64Element(Message message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
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
    * Sets the element to the "value" passed and immediately sends the message that contains it..
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

   @Override
   public Double getValue() {
      return Double.longBitsToDouble(getMsgData().getMem().getLong(byteOffset, msb, lsb));
   }
   
   @Override
   public Double getBitValue(int msb, int lsb) {
      return Double.longBitsToDouble(getMsgData().getMem().getLong(byteOffset, msb, lsb));
   }

   @Override
   public Double valueOf(MemoryResource mem) {
      return Double.longBitsToDouble(mem.getLong(byteOffset, msb, lsb));
   }

   @Override
   public void setValue(Double obj) {
      getMsgData().getMem().setLong(Double.doubleToLongBits(obj), byteOffset, msb, lsb);
   }

   /**
    * Sets the element to the "value" passed.
    * 
    * @param accessor - For logging.
    * @param bits - The bits to set.
    */
   public void setRawBits(ITestEnvironmentAccessor accessor, long bits) {
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, this.getFullName(), new MethodFormatter().add(bits),
            this.getMessage());
      }

      getMsgData().getMem().setLong(bits, byteOffset, msb, lsb);
      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
   }

   /**
    * Sest the element without the logging.
    */
   public void setRawBits(long bits) {
      getMsgData().getMem().setLong(bits, byteOffset, msb, lsb);
   }

   @Override
   protected double toDouble(long value) {
      return Double.longBitsToDouble(value);
   }

   @Override
   protected long toLong(double value) {
      return Double.doubleToLongBits(value);
   }

   @Override
   protected Element getNonMappingElement() {
      return new NonMappingFloat64Element(this);
   }

   @Override
   public void setHex(long hex) {
      getMsgData().getMem().setLong(hex, byteOffset, msb, lsb);
   }
}

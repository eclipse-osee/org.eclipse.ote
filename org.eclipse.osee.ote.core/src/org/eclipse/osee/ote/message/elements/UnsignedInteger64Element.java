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

/**
 * @author Michael P. Masterson
 */
public class UnsignedInteger64Element extends NumericElement<Long> {

   public UnsignedInteger64Element(Message msg, String elementName, MessageData messageData,
         int byteOffset, int msb, int lsb) {
      super(msg, elementName, messageData, byteOffset, msb, lsb);
   }

   @Override
   public long getNumericBitValue() {
      return getValue().longValue();
   }

   @Override
   public void setValue(Long value) {
      getMsgData().getMem().setLong(value, byteOffset, msb, lsb);
   }

   @Override
   public Long getValue() {
      return getMsgData().getMem().getLong(byteOffset, msb, lsb);
   }

   @Override
   public String toString(Long obj) {
      return obj.toString();
   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      this.set(accessor, Long.parseLong(value));

   }

   @Override
   public Long valueOf(MemoryResource mem) {
      return Long.valueOf(mem.getLong(byteOffset, msb, lsb));
   }

   @Override
   public Long elementMask(Long value) {
      return value;
   }

   @Override
   protected Element getNonMappingElement() {
      return this;
   }

   public void setLong(long value) {
      this.setValue(Long.valueOf(value));
   }

   public void setNoLog(long value) {
      setValue(Long.valueOf(value));
   }

   @Override
   public UnsignedInteger64Element switchMessages(Collection<? extends Message> messages) {
      return (UnsignedInteger64Element) super.switchMessages(messages);
   }

}

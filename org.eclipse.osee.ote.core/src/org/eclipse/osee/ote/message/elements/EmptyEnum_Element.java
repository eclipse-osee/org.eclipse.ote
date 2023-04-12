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

import java.util.Collection;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingEmptyEnumElement;
import org.eclipse.osee.ote.message.enums.EmptyEnum;

/**
 * @author Michael P. Masterson
 */
public class EmptyEnum_Element extends DiscreteElement<EmptyEnum> {

   public EmptyEnum_Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public EmptyEnum_Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   public EmptyEnum_Element(Message message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
   }

   @Override
   @Deprecated
   public EmptyEnum_Element findElementInMessages(Collection<? extends Message> messages) {
      return (EmptyEnum_Element) super.findElementInMessages(messages);
   }
   
   @Override
   public EmptyEnum_Element switchMessages(Collection<? extends Message> messages) {
      return (EmptyEnum_Element) super.switchMessages(messages);
   }

   /**
    * Sets the element to the "value" passed and immediately sends the message that contains it.
    * 
    * @param enumeration The value to set.
    */
   public void setAndSend(ITestEnvironmentAccessor accessor, EmptyEnum enumeration) {
      this.set(accessor, enumeration);
      super.sendMessage();
   }

   @Override
   protected Element getNonMappingElement() {
      return new NonMappingEmptyEnumElement(this);
   }

   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asEmptyEnumElement(this);
   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      int intValue = Integer.parseInt(value);
      getMsgData().getMem().setInt(intValue, byteOffset, msb, lsb);
   }

   @Override
   public EmptyEnum getValue() {
      return toEnum(getMsgData().getMem().getInt(byteOffset, msb, lsb));
   }
   
   @Override
   public EmptyEnum getBitValue(int msb, int lsb) {
      return toEnum(getMsgData().getMem().getInt(byteOffset, msb, lsb));
   }

   @Override
   public void setValue(EmptyEnum obj) {
      getMsgData().getMem().setInt(obj.getValue(), byteOffset, msb, lsb);
   }

   @Override
   public String toString(EmptyEnum obj) {
      return "EmptyEnum_" + obj.getValue();
   }

   @Override
   public EmptyEnum valueOf(MemoryResource mem) {
      return toEnum(mem.getInt(byteOffset, msb, lsb));
   }

   private EmptyEnum toEnum(int intValue) {
      return EmptyEnum.toEnum(intValue);
   }

   @Override
   public EmptyEnum elementMask(EmptyEnum value) {
      return value;
   }

}

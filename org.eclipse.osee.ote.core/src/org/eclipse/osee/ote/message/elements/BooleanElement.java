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

public class BooleanElement extends DiscreteElement<Boolean> {

   public BooleanElement(Message msg, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalMsb, int originalLsb) {
      super(msg, elementName, messageData, byteOffset, msb, lsb, originalMsb, originalLsb);

   }

   public BooleanElement(Message msg, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(msg, elementName, messageData, bitOffset, bitLength);

   }

   public BooleanElement(Message msg, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(msg, elementName, messageData, byteOffset, msb, lsb);

   }

   @Override
   protected Element getNonMappingElement() {
      return null;
   }

   @Override
   public Boolean getValue() {
      return Boolean.valueOf(getMsgData().getMem().getBoolean(byteOffset, msb, lsb));
   }

   @Override
   public void setValue(Boolean obj) {
      getMsgData().getMem().setBoolean(obj, byteOffset, msb, lsb);
   }

   @Override
   public String toString(Boolean obj) {
      return obj.toString();
   }

   @Override
   public Boolean valueOf(MemoryResource mem) {
      return Boolean.valueOf(mem.getBoolean(byteOffset, msb, lsb));
   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      set(accessor, Boolean.parseBoolean(value));
   }

   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asBooleanElement(this);
   }

   @Override
   public Boolean elementMask(Boolean value) {
      return value;
   }
   
   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.elements.Element#switchMessages(java.util.Collection)
    */
   @Override
   public BooleanElement switchMessages(Collection<? extends Message> messages) {
      return (BooleanElement) super.switchMessages(messages);
   }
   
}

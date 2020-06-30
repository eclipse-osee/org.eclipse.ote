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

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingIntegerElement;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 * @deprecated Please use {@link UnsignedIntegerElement} instead
 */
public class IntegerElement extends UnsignedIntegerElement {

   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asIntegerElement(this);
   }

   public IntegerElement(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public IntegerElement(Message message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
   }

   public IntegerElement(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }
   
   @Override
   protected IntegerElement getNonMappingElement() {
      return new NonMappingIntegerElement(this);
   }

   @Override
   public IntegerElement findElementInMessages(Collection<? extends Message> messages) {
      return (IntegerElement) super.findElementInMessages(messages);
   }
   
   @Override
   public IntegerElement switchMessages(Collection<? extends Message> messages) {
      return (IntegerElement) super.switchMessages(messages);
   }


}
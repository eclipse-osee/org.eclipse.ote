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

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingSignedIntegerElement;

/**
 * @author Michael P. Masterson
 */
public class SignedInteger16Element extends SignedIntegerElement {

   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asSignedInteger16Element(this);
   }

   public SignedInteger16Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public SignedInteger16Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb) {
      super(message, elementName, messageData, byteOffset, msb, msb + 15);
   }

   public SignedInteger16Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   @Override
   public SignedInteger16Element findElementInMessages(Collection<? extends Message> messages) {
      return (SignedInteger16Element) super.findElementInMessages(messages);
   }
   
   @Override
   public SignedInteger16Element switchMessages(Collection<? extends Message> messages) {
      return (SignedInteger16Element) super.switchMessages(messages);
   }


   @Override
   protected SignedIntegerElement getNonMappingElement() {
      return new NonMappingSignedIntegerElement(this);
   }

}
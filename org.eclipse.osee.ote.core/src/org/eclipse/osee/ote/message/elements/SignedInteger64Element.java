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
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingSignedLong64Element;

/**
 * @author Michael P. Masterson
 */
public class SignedInteger64Element extends SignedLongIntegerElement {

   public SignedInteger64Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb) {
      super(message, elementName, messageData, byteOffset, msb, msb + 63);
   }
   
   @Override
   public SignedInteger64Element findElementInMessages(Collection<? extends Message> messages) {
      return (SignedInteger64Element) super.findElementInMessages(messages);
   }
   
   @Override
   public SignedInteger64Element switchMessages(Collection<? extends Message> messages) {
      return (SignedInteger64Element) super.switchMessages(messages);
   }
   
   @Override
   protected SignedLongIntegerElement getNonMappingElement() {
      return new NonMappingSignedLong64Element(this);
   }

}

/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message.elements;

import java.util.Collection;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;

public class InaccessibleElement extends Element {

   public InaccessibleElement(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public InaccessibleElement(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   @Override
   public InaccessibleElement findElementInMessages(Collection<? extends Message> messages) {
      return (InaccessibleElement) super.findElementInMessages(messages);
   }
   
   @Override
   public InaccessibleElement switchMessages(Collection<? extends Message<?,?,?>> messages) {
      return (InaccessibleElement) super.switchMessages(messages);
   }

   @Override
   protected Element getNonMappingElement() {
      return null;
   }

   public int compareTo(InaccessibleElement o) {
      return 0;
   }

}

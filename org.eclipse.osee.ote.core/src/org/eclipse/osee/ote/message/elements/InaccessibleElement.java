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

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

/**
 * @author Michael P. Masterson
 */
public class SignedInteger8Element extends SignedIntegerElement {

   public SignedInteger8Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb) {
      super(message, elementName, messageData, byteOffset, msb, msb + 7);
   }
   
   @Override
   public SignedInteger8Element findElementInMessages(Collection<? extends Message> messages) {
      return (SignedInteger8Element) super.findElementInMessages(messages);
   }
   
   @Override
   public SignedInteger8Element switchMessages(Collection<? extends Message> messages) {
      return (SignedInteger8Element) super.switchMessages(messages);
   }
   
}

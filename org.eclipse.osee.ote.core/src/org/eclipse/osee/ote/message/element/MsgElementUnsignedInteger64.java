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
package org.eclipse.osee.ote.message.element;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.elements.UnsignedInteger64Element;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;

/**
 * Represent any message field as an unsigned integer from 1 to 63 bits in length
 * 
 * @author Michael P. Masterson
 */
public class MsgElementUnsignedInteger64 extends MsgElementNumeric<Long> {

   /**
    * @param sourceMessageClass
    * @param sourceElement
    * @param requestor 
    */
   public MsgElementUnsignedInteger64(Class<? extends Message> sourceMessageClass, UnsignedInteger64Element sourceElement, IMessageRequestor<Message> requestor) {
      super(sourceMessageClass, sourceElement, requestor);
   }

}
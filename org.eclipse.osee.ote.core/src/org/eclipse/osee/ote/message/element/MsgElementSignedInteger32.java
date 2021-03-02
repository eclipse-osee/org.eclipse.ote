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
import org.eclipse.osee.ote.message.elements.SignedInteger32Element;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;

/**
 * Represent any message field as an signed integer from 1 to 32 bits in length
 * 
 * @author Michael P. Masterson
 */
public class MsgElementSignedInteger32 extends MsgElementNumeric<Integer> {

   /**
    * @param sourceMessageClass
    * @param sourceElement
    * @param requestor 
    */
   public MsgElementSignedInteger32(Class<? extends Message> sourceMessageClass, SignedInteger32Element sourceElement, IMessageRequestor<Message> requestor) {
      super(sourceMessageClass, sourceElement, requestor);
   }

}
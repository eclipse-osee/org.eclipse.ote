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
import org.eclipse.osee.ote.message.elements.NumericElement;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;

/**
 * @author Michael P. Masterson
 * @param <T> The base comparable type backing the element
 */
public abstract class MsgElementNumeric<T extends Number & Comparable<T>> extends MsgElementDiscrete<T> {

   /**
    * @param sourceMessageClass
    * @param sourceElement
    * @param requestor 
    */
   public MsgElementNumeric(Class<? extends Message> sourceMessageClass, NumericElement<T> sourceElement, IMessageRequestor<Message> requestor) {
      super(sourceMessageClass, sourceElement, requestor);
   }

   public long getNumericBitValue() {
      return ((NumericElement<T>) getElementToRead()).getNumericBitValue();
   }

}

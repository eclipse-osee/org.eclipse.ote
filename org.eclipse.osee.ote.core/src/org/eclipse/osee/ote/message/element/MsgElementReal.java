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
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.RealElement;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;

/**
 * @author Michael P. Masterson
 */
public abstract class MsgElementReal extends MsgElementNumeric<Double> {

   /**
    * @param sourceMessageClass
    * @param sourceElement
    * @param requestor 
    */
   public MsgElementReal(Class<? extends Message> sourceMessageClass, RealElement sourceElement, IMessageRequestor<Message> requestor) {
      super(sourceMessageClass, sourceElement, requestor);
   }

   public void setNoLog(Float value) {
      setNoLog(value.doubleValue());
   }

   public long getRaw() {
      return ((RealElement) getElementToRead()).getRaw();
   }

   public long getRaw(MemoryResource mem) {
      return ((RealElement) getElementToRead()).getRaw(mem);
   }

   /**
    * sets the bit pattern for this element. All hex values must be in the form of: <br>
    * <p>
    * <code><b>0x[<I>hex characters</I>]L</b></code><br>
    * <p>
    * The trailing 'L' signals java to treat the value as a long integer.
    * 
    * @param hex a bit patter to set the element to. The pattern is not limited to hexadecimal
    */
   public void setHex(long hex) {
      ((RealElement) getElementToWrite()).setHex(hex);
   }

}
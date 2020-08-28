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

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.elements.CharElement;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;

/**
 * Represent any 8 bit message field as an ASCII Character
 * 
 * @author Michael P. Masterson
 */
public class MsgElementChar extends MsgElementDiscrete<Character> {

   /**
    * @param sourceMessageClass
    * @param sourceElement
    * @param requestor 
    */
   public MsgElementChar(Class<? extends Message> sourceMessageClass, CharElement sourceElement, IMessageRequestor<Message> requestor) {
      super(sourceMessageClass, sourceElement, requestor);
   }

   /**
    * Helper method that allows for the setting of contiguous CharElements as if they were a string.
    * 
    * @param accessor
    * @param value
    */
   public void set(ITestEnvironmentAccessor accessor, String value) {
      parseAndSet(accessor, value);
   }

   /**
    * Returns the string of length "stringLength" starting as the position of the element. Assumes that there are enough
    * CharElements in a row to fill in the string.
    * 
    * @param accessor for logging
    * @param stringLength the length of the string to return
    * @return the string starting with this element
    */
   public String getString(ITestEnvironmentAccessor accessor, int stringLength) {
      return ((CharElement) getElementToRead()).getString(accessor, stringLength);
   }

}
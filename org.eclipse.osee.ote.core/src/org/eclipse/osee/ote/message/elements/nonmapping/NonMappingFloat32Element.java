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
package org.eclipse.osee.ote.message.elements.nonmapping;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.Float32Element;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Andy Jury
 */
public class NonMappingFloat32Element extends Float32Element {

   /**
    * Copy constructor.
    */
   public NonMappingFloat32Element(Float32Element element) {
      super(element.getMessage(), element.getElementName(), element.getMsgData(), element.getByteOffset(),
         element.getMsb(), element.getLsb());
      for (Object obj : element.getElementPath()) {
         this.getElementPath().add(obj);
      }
   }

   public NonMappingFloat32Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb);
   }

   public NonMappingFloat32Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   public NonMappingFloat32Element(Message message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
   }

   @Override
   public void checkForwarding(ITestAccessor accessor, Float32Element cause, double value) throws InterruptedException {
      throwNoMappingElementException();
   }

   @Override
   public boolean isNonMappingElement() {
      return true;
   }

}

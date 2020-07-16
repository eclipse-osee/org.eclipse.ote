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

package org.eclipse.osee.ote.message.elements.nonmapping;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.FixedPointElement;

/**
 * @author Andy Jury
 */
public class NonMappingFixedPointElement extends FixedPointElement {

   /**
    * Copy constructor.
    */
   public NonMappingFixedPointElement(FixedPointElement element) {
      super(element.getMessage(), element.getElementName(), element.getMsgData(), 0, false, element.getByteOffset(),
         element.getMsb(), element.getLsb());
      for (Object obj : element.getElementPath()) {
         this.getElementPath().add(obj);
      }
   }

   public NonMappingFixedPointElement(Message message, String elementName, MessageData messageData, double resolution, boolean signed, int byteOffset, int msb, int lsb) {
      super(message, elementName, messageData, resolution, signed, byteOffset, msb, lsb);
   }

   public NonMappingFixedPointElement(Message message, String elementName, MessageData messageData, double resolution, double minVal, boolean signed, int byteOffset, int msb, int lsb) {
      super(message, elementName, messageData, resolution, minVal, signed, byteOffset, msb, lsb);
   }

   public NonMappingFixedPointElement(Message message, String elementName, MessageData messageData, double resolution, boolean signed, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, resolution, signed, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   public NonMappingFixedPointElement(Message message, String elementName, MessageData messageData, double resolution, double minVal, boolean signed, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, resolution, minVal, signed, byteOffset, msb, lsb, originalLsb,
         originalMsb);
   }

   public NonMappingFixedPointElement(Message message, String elementName, MessageData messageData, double resolution, double minVal, boolean signed, int bitOffset, int bitLength) {
      super(message, elementName, messageData, resolution, minVal, signed, bitOffset, bitLength);
   }

   /**
    * @return Returns the minVal.
    */
   @Override
   public double getMinVal() {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * @return Returns the resolution.
    */
   @Override
   public double getResolution() {
      throwNoMappingElementException();
      return 0;
   }

   /**
    * @return Returns the signed.
    */
   @Override
   public boolean isSigned() {
      throwNoMappingElementException();
      return false;
   }

   @Override
   public boolean isNonMappingElement() {
      return true;
   }
}

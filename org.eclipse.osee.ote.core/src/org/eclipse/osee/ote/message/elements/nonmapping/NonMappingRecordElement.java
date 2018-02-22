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
import org.eclipse.osee.ote.message.elements.RecordElement;

/**
 * @author Andy Jury
 */
public class NonMappingRecordElement extends RecordElement {

   public NonMappingRecordElement(RecordElement element) {
      super(element.getMessage(), element.getElementName(), element.getMsgData(), element.getByteOffset(),
         element.getBitLength());
      for (Object obj : element.getElementPath()) {
         this.getElementPath().add(obj);
      }
   }

   public NonMappingRecordElement(Message message, String elementName, int index, MessageData messageData, int firstRecordBitOffset, int recordBitSize) {
      super(message, elementName, index, messageData, firstRecordBitOffset, recordBitSize);
   }

   public NonMappingRecordElement(RecordElement message, String elementName, int offset, MessageData messageData, int firstRecordByteOffset, int recordByteSize) {
      super(message, elementName, offset, messageData, firstRecordByteOffset, recordByteSize);
   }

   public NonMappingRecordElement(Message message, String elementName, MessageData messageData, int firstRecordByteOffset, int recordByteSize) {
      super(message, elementName, messageData, firstRecordByteOffset, recordByteSize);
   }

   @Override
   public boolean isNonMappingElement() {
      return true;
   }

   public int compareTo(RecordElement o) {
      throwNoMappingElementException();
      return 0;
   }

}

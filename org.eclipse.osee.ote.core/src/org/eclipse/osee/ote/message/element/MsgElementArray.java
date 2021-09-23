/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import java.nio.ByteBuffer;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.ArrayElement;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;

/**
 * Represents elements as an array of bytes.
 * 
 * @author Michael P. Masterson
 */
public class MsgElementArray {
   private final Class<? extends Message> sourceMessageClass;
   private final ArrayElement sourceElement;
   private final IMessageRequestor<Message> requestor;
   private Message sourceMessageWriter;
   private Message sourceMessageReader;
   private ArrayElement elementToWrite;
   private ArrayElement elementToRead;

   public MsgElementArray(Class<? extends Message> sourceMessageClass, ArrayElement sourceElement, IMessageRequestor<Message> requestor) {
      this.sourceMessageClass = sourceMessageClass;
      this.sourceElement = sourceElement;
      this.requestor = requestor;
   }

   protected ArrayElement getElementToWrite() {
      if (sourceMessageWriter == null || sourceMessageWriter.isDestroyed() || elementToWrite == null) {
         sourceMessageWriter = requestor.getMessageWriter(sourceMessageClass);
         Class<? extends ArrayElement> clazz = sourceElement.getClass();
         elementToWrite = sourceMessageWriter.getElement(sourceElement.getElementName(), clazz);

         // May be a record so use path instead
         if (elementToWrite == null) {
            elementToWrite = clazz.cast(sourceMessageWriter.getElement(sourceElement.getElementPath()));
         }
      }
      return elementToWrite;
   }

   protected ArrayElement getElementToRead() {
      if (sourceMessageReader == null || sourceMessageReader.isDestroyed() || elementToRead == null) {
         sourceMessageReader = requestor.getMessageReader(sourceMessageClass);
         Class<? extends ArrayElement> clazz = sourceElement.getClass();
         elementToRead = sourceMessageReader.getElement(sourceElement.getElementName(), clazz);

         // May be a record so use path instead
         if (elementToRead == null) {
            elementToRead = clazz.cast(sourceMessageReader.getElement(sourceElement.getElementPath()));
         }
      }
      return elementToRead;
   }

   public void setValue(int index, byte value) {
      getElementToRead().setValue(index, value);
   }

   public ByteBuffer asByteBuffer() {
      return getElementToRead().asByteBuffer();
   }

   public byte getValue(int index) {
      return getElementToRead().getValue(index);
   }

   public byte getValue(MemoryResource mem, int index) {
      return getElementToRead().getValue(mem, index);

   }

   public void zeroize() {
      getElementToWrite().zeroize();
      getElementToRead().zeroize();
   }

   public int getLength() {
      return getElementToRead().getLength();
   }

   public int getArrayStartOffset() {
      return getElementToRead().getArrayStartOffset();
   }

   public int getArrayEndOffset() {
      return getElementToRead().getArrayEndOffset();
   }

}

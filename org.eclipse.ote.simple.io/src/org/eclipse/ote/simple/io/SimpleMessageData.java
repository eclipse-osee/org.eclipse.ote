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

package org.eclipse.ote.simple.io;


import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.IMessageDataVisitor;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.messaging.dds.Data;

/**
 * Contains the byte data for the Simple Message
 * 
 * @author Michael P. Masterson
 */
public class SimpleMessageData extends MessageData implements Data {
   /**
    * Messages do not need to include a header
    */
   private final SimpleMessageHeader header;

   public SimpleMessageData(Message<?, ?, ?> msg, String typeName, String name, int dataByteSize, DataType type) {
      super(typeName, 
            name, 
            dataByteSize + SimpleMessageHeader.HEADER_BYTE_SIZE,
            SimpleMessageHeader.HEADER_BYTE_SIZE, 
            type);
      header = new SimpleMessageHeader(msg, getMem().slice(0,SimpleMessageHeader.HEADER_BYTE_SIZE));
      initializeDefaultHeaderValues();
   }


   @Override
   public int getPayloadSize() {
      return super.getPayloadSize() - SimpleMessageHeader.HEADER_BYTE_SIZE;
   }

   public int getHeaderSize() {
      return SimpleMessageHeader.HEADER_BYTE_SIZE;
   }

   @Override
   public SimpleMessageHeader getMsgHeader() {
      return header;
   }

   @Override
   public void initializeDefaultHeaderValues() {
      header.NAME.setNoLog(getName());
   }

   /**
    * Can also only send when data changes by using getMem().isDataChanged()
    */
   @Override
   protected boolean shouldSendData() {
      return true;
   }


   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.data.MessageData#visit(org.eclipse.osee.ote.message.data.IMessageDataVisitor)
    */
   @Override
   public void visit(IMessageDataVisitor visitor) {
      // Intentionally empty
   }

}

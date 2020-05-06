/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

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
   private final SimpleMessageHeader header;
   private String name;


   public SimpleMessageData(Message<?, ?, ?> msg, String typeName, String name, int dataByteSize, DataType type) {
      super(typeName, 
            name, 
            dataByteSize + SimpleMessageHeader.HEADER_BYTE_SIZE,
            SimpleMessageHeader.HEADER_BYTE_SIZE, 
            type);
      header = new SimpleMessageHeader(msg, getMem().slice(0,SimpleMessageHeader.HEADER_BYTE_SIZE));
      initializeDefaultHeaderValues();
      this.name = name;
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
      header.NAME.setNoLog(name);
   }

   @Override
   protected boolean shouldSendData() {
      return getMem().isDataChanged();
   }


   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.data.MessageData#visit(org.eclipse.osee.ote.message.data.IMessageDataVisitor)
    */
   @Override
   public void visit(IMessageDataVisitor visitor) {
      // Intentionally empty
   }

}

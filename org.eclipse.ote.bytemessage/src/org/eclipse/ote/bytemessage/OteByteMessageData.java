/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.bytemessage;

import org.eclipse.osee.ote.message.data.IMessageDataVisitor;
import org.eclipse.osee.ote.message.data.MessageData;




public class OteByteMessageData extends MessageData{
   
   private final OteByteMessageHeader header;
   
   public OteByteMessageData(OteByteMessage msg, String topic, int messageId, int dataByteSize) {
      super(msg.getName(), OteByteMessageHeader.HEADER_SIZE + dataByteSize, OteByteMessageHeader.HEADER_SIZE, OteByteMessageType.OTE_BYTE_MESSAGE);
      this.header = new OteByteMessageHeader(msg, topic, messageId, getMem().slice(0, OteByteMessageHeader.HEADER_SIZE));
   }
   
   public OteByteMessageData(OteByteMessage msg, int dataByteSize) {
	   super("default", OteByteMessageHeader.HEADER_SIZE + dataByteSize, OteByteMessageHeader.HEADER_SIZE, OteByteMessageType.OTE_BYTE_MESSAGE);
	   this.header = new OteByteMessageHeader(msg, "", 0, getMem().slice(0, OteByteMessageHeader.HEADER_SIZE));
   }

@Override
   public OteByteMessageHeader getMsgHeader() {
      return header;
   }

   @Override
   public void initializeDefaultHeaderValues() {
   }

   @Override
   public int getPayloadSize() {
      return super.getDefaultDataByteSize() - OteByteMessageHeader.HEADER_SIZE;
   }

   @Override
   public void visit(IMessageDataVisitor visitor) {
   }
}

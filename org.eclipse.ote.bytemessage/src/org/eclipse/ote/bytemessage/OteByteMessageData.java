/*********************************************************************
 * Copyright (c) 2013 Boeing
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

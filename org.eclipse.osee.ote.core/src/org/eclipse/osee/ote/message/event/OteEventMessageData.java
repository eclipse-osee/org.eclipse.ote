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

package org.eclipse.osee.ote.message.event;

import org.eclipse.osee.ote.message.data.IMessageDataVisitor;
import org.eclipse.osee.ote.message.data.MessageData;


public class OteEventMessageData extends MessageData{
   
   private final OteEventMessageHeader header;
   
   public OteEventMessageData(OteEventMessage msg, String topic, int dataByteSize) {
      super(msg.getName(), OteEventMessageHeader.HEADER_SIZE + dataByteSize, OteEventMessageHeader.HEADER_SIZE, OteEventMessageType.OTE_EVENT_MESSAGE);
      this.header = new OteEventMessageHeader(msg, topic, 0, getMem().slice(0, OteEventMessageHeader.HEADER_SIZE));
   }
   
   public OteEventMessageData(OteEventMessage msg, int dataByteSize) {
	   super("default", OteEventMessageHeader.HEADER_SIZE + dataByteSize, OteEventMessageHeader.HEADER_SIZE, OteEventMessageType.OTE_EVENT_MESSAGE);
	   this.header = new OteEventMessageHeader(msg, "", 0, getMem().slice(0, OteEventMessageHeader.HEADER_SIZE));
   }

@Override
   public OteEventMessageHeader getMsgHeader() {
      return header;
   }

   @Override
   public void initializeDefaultHeaderValues() {
   }

   @Override
   public int getPayloadSize() {
      return super.getDefaultDataByteSize() - OteEventMessageHeader.HEADER_SIZE;
   }

	@Override
	public void visit(IMessageDataVisitor visitor) {
	}
}

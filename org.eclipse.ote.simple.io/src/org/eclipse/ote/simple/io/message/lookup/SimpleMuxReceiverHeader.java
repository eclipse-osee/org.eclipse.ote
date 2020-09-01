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
package org.eclipse.ote.simple.io.message.lookup;

import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.IMessageDataVisitor;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.BooleanElement;
import org.eclipse.osee.ote.message.elements.UnsignedInteger32Element;
import org.eclipse.ote.io.GenericOteIoType;
import org.eclipse.ote.io.mux.MuxMessage;
import org.eclipse.ote.io.mux.MuxReceiveTransmit;

/**
 * @author Michael P. Masterson
 */
public class SimpleMuxReceiverHeader extends Message {
   
   private static final int BYTE_SIZE = 2;
   
   public UnsignedInteger32Element channel;
   public UnsignedInteger32Element remoteTerminal;
   public BooleanElement isReceive;
   public UnsignedInteger32Element subaddress;

   public SimpleMuxReceiverHeader() {
      super(SimpleMuxReceiverHeader.class.getName(), BYTE_SIZE, 0, false, 0, 0.0);
      
      MessageData messageData = new ReceiverMessageData(SimpleMuxReceiverHeader.class.getName(), BYTE_SIZE, 0);
      setDefaultMessageData(messageData);
      channel = new UnsignedInteger32Element(this, "channel", messageData, 0, 0, 2);
      remoteTerminal = new UnsignedInteger32Element(this, "remoteTerminal", messageData, 0, 3, 7);
      isReceive = new BooleanElement(this, "channel", messageData, 1, 0, 0);
      subaddress = new UnsignedInteger32Element(this, "subaddress", messageData, 1, 1, 5);
      
      setCurrentMemType(GenericOteIoType.MUX);

   }
   
   public void fillInBytes(MuxMessage msg) {
      this.channel.setNoLog((int) msg.getChannelNumber());
      this.remoteTerminal.setNoLog((int) msg.getTerminalNumber());
      this.isReceive.setNoLog(msg.getTransmitFlag() == MuxReceiveTransmit.RECEIVE);
      this.subaddress.setNoLog((int) msg.getSubAddress());
   }
   
   @Override
   public String toString() {
      return String.format("CH %02d %02d%s%02d", channel.getNoLog(), remoteTerminal.getNoLog(), isReceive.getNoLog()?"R":"T", subaddress.getNoLog());
   }
   
   private class ReceiverMessageData extends MessageData {

      public ReceiverMessageData(String name, int dataByteSize, int offset) {
         super(name, dataByteSize, offset, GenericOteIoType.MUX);
      }

      @Override
      public IMessageHeader getMsgHeader() {
         return null;
      }

      @Override
      public void visit(IMessageDataVisitor visitor) {
      }

      @Override
      public void initializeDefaultHeaderValues() {
      }
      
   }

}

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

package org.eclipse.ote.io.mux;


import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.IMessageDataVisitor;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.messaging.dds.entity.DataWriter;

/**
 * @author Andrew M. Finkbeiner
 * @author Michael P. Masterson
 */
public class MuxData extends MessageData {
   private final int channelNumber;
   private final int remoteTerminalNumber;
   private final MuxReceiveTransmit receiveTransmitFlag;
   private final int subaddressNumber;
   private final MuxHeader header;
   private int wordCount = 32;

   public MuxData(Message msg, String typeName, String name, int dataByteSize, int channelNumber, int remoteTerminalNumber, MuxReceiveTransmit receiveTransmitFlag, int subaddressNumber, DataType type) {
      super(typeName, name, dataByteSize + MuxHeader.MUX_HEADER_BYTE_SIZE, MuxHeader.MUX_HEADER_BYTE_SIZE, type);
      this.channelNumber = channelNumber;
      this.remoteTerminalNumber = remoteTerminalNumber;
      this.receiveTransmitFlag = receiveTransmitFlag;
      this.subaddressNumber = subaddressNumber;
      header = new MuxHeader(msg, getMem().slice(0, MuxHeader.MUX_HEADER_BYTE_SIZE));
      wordCount = dataByteSize / 2;
   }

   @Override
   public int getPayloadSize() {
      return wordCount * 2;
   }

   public int getHeaderSize() {
      return MuxHeader.MUX_HEADER_BYTE_SIZE;
   }

   @Override
   public void setWriter(DataWriter writer) {
      super.setWriter(writer);
      writer.setPublishBackToLocalDDSReaders(false);
   }

   @Override
   public MuxHeader getMsgHeader() {
      return header;
   }

   public int getChannelNumber() {
      return channelNumber;
   }

   public MuxReceiveTransmit getReceiveTransmitFlag() {
      return receiveTransmitFlag;
   }

   public int getRemoteTerminalNumber() {
      return remoteTerminalNumber;
   }

   public int getSubaddressNumber() {
      return subaddressNumber;
   }

   /**
    * @return Returns the wordCount.
    */
   public int getWordCount() {
      return wordCount;
   }

   /**
    * @param wordCount The wordCount to set.
    */
   public void setWordCount(int wordCount) {
      if (wordCount < 0 || wordCount > 32) {
         throw new IllegalArgumentException("Number of words must be between 0 and 32");
      }
      this.wordCount = wordCount;
   }

   @Override
   public void initializeDefaultHeaderValues() {
      header.MUX_PORT_NUMBER.setNoLog(this.channelNumber);
      header.REMOTE_TERMINAL_1.setNoLog(this.remoteTerminalNumber);
      header.SUBADDRESS_1.setNoLog(this.subaddressNumber);
      header.DIRECTION_1.setNoLog(this.receiveTransmitFlag.getValue());
   }

   @Override
   protected boolean shouldSendData() {
      return getMem().isDataChanged();
   }
   
   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.data.MessageData#toString()
    */
   @Override
   public String toString() {
      return String.format("Channel %d, %02d%s%02d", getChannelNumber(), getRemoteTerminalNumber(), getReceiveTransmitFlag() == MuxReceiveTransmit.RECEIVE?"R":"T", getSubaddressNumber());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.data.MessageData#visit(org.eclipse.osee.ote.message.data.IMessageDataVisitor)
    */
   /**
    * May be overridden if visitor pattern required
    */
   @Override
   public void visit(IMessageDataVisitor visitor) {
      // do nothing
   }
}

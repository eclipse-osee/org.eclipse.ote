/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.io.pubsub;

import org.eclipse.osee.framework.jdk.core.util.ByteUtil;
import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.Float64Element;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Michael P. Masterson
 */
public class PubSubHeader implements IMessageHeader {

   public static final int HEADER_BYTE_SIZE = 54;
   public static final int MESSAGE_NAME_BYTE_SIZE = 28;
   public static final int MAX_DATA_BYTE_SIZE = 4096;

   public final IntegerElement MSG_ID;
   public final IntegerElement HEADER_SIZE;
   public final IntegerElement SOURCE_ID;
   public final IntegerElement DEST_ID;

   public final IntegerElement MSG_DATA_SIZE;
   public final Float64Element TIME_TAG;
   public final IntegerElement SEQUENCE_NUM;
   public final StringElement MSG_NAME;

   private final HeaderData mData;
   private final String messageName;

   public PubSubHeader(Message msg, String name, MemoryResource memoryResource) {
      this.messageName = name;
      mData = new HeaderData(name + ".HEADER", memoryResource);

      Object[] path = new Object[] {(msg == null ? "message" : msg.getClass().getName()), "HEADER(PUBSUB)"};

      MSG_ID = new IntegerElement(msg, "MSG_ID", mData, 0, 0, 31);
      HEADER_SIZE = new IntegerElement(msg, "HEADER_SIZE", mData, 4, 0, 15);
      MSG_DATA_SIZE = new IntegerElement(msg, "MSG_DATA_SIZE", mData, 6, 0, 31);
      SOURCE_ID = new IntegerElement(msg, "SOURCE_SU_ID", mData, 10, 0, 15);
      DEST_ID = new IntegerElement(msg, "DEST_SU_ID", mData, 12, 0, 15);
      TIME_TAG = new Float64Element(msg, "TIME_TAG", mData, 14, 0, 63);
      SEQUENCE_NUM = new IntegerElement(msg, "SEQUENCE_NUM", mData, 22, 0, 31);
      MSG_NAME = new StringElement(msg, "MSG_NAME", mData, 26, 0, MESSAGE_NAME_BYTE_SIZE * 8);

      MSG_ID.addPath(path);
      HEADER_SIZE.addPath(path);
      MSG_DATA_SIZE.addPath(path);
      SOURCE_ID.addPath(path);
      DEST_ID.addPath(path);
      TIME_TAG.addPath(path);
      SEQUENCE_NUM.addPath(path);
      MSG_NAME.addPath(path);

      HEADER_SIZE.setNoLog(HEADER_BYTE_SIZE);
   }

   @Override
   public void setNewBackingBuffer(byte[] data) {
      mData.setNewBackingBuffer(data);
   }

   @Override
   public Element[] getElements() {
      return new Element[] {
         MSG_ID,
         HEADER_SIZE,
         MSG_DATA_SIZE,
         SOURCE_ID,
         DEST_ID,
         TIME_TAG,
         SEQUENCE_NUM,
         MSG_NAME};
   }

   @Override
   public byte[] getData() {
      return mData.toByteArray();
   }

   protected void endLogging(ITestAccessor accessor) {

      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
   }

   @Override
   public String toXml() {
      StringBuilder builder = new StringBuilder();
      builder.append(String.format(
         "<PubSubHeaderInfo messageName=\"%s\" "
            + "messageId=\"%d\" "
            + "timeTag=\"%f\" "
            + "sequenceNum=\"%d\" "
            + "messageDataByteSize=\"%d\" "
            + "hdrByteSize=\"%d\" "
            + "srcId=\"%d\" "
            + "destId=\"%d\" \n",
            MSG_NAME.getNoLog(),
            MSG_ID.getNoLog(),
            TIME_TAG.getNoLog(),
            SEQUENCE_NUM.getNoLog(),
            MSG_DATA_SIZE.getNoLog(),
            HEADER_SIZE.getNoLog(),
            SOURCE_ID.getNoLog(),
            DEST_ID.getNoLog()));
      ByteUtil.printByteDump(builder, this.getData(), 0, HEADER_BYTE_SIZE, 16);
      builder.append("</PubSubHeaderInfo>");
      return builder.toString();
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(String.format(
         "PubSubHeaderInfo \n\tmessageName=\"%s\" \n\tmessageId=\"%d\" \n\tmessageDataByteSize=\"%d\" " + "\n\thdrByteSize=\"%d\" ",
         MSG_NAME.getNoLog(), MSG_ID.getNoLog(), MSG_DATA_SIZE.getNoLog(), HEADER_SIZE.getNoLog()));
      ByteUtil.printByteDump(builder, this.getData(), 0, HEADER_BYTE_SIZE, 16);
      return builder.toString();
   }

   @Override
   public String getMessageName() {
      return messageName;
   }

   @Override
   public int getHeaderSize() {
      return this.HEADER_SIZE.getNoLog();
   }

}

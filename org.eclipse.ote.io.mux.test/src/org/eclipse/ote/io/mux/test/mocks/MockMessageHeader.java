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

package org.eclipse.ote.io.mux.test.mocks;

import org.eclipse.osee.framework.jdk.core.util.ByteUtil;
import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.StringElement;

/**
 * @author Michael P. Masterson
 */
public class MockMessageHeader implements IMessageHeader {
   public static final int HEADER_BYTE_SIZE = 32;
   private final HeaderData headerData;
   
   public final StringElement NAME;

   public MockMessageHeader(Message msg, MemoryResource data) {
      headerData = new HeaderData("MockMessageHeader", data);

      Object[] path = new Object[]{(msg == null ? "message" : msg.getClass().getName()), "HEADER(SIMPLE)"};
      NAME = new StringElement(msg, "NAME", headerData, 0, 0, HEADER_BYTE_SIZE * 8 - 1);
      NAME.addPath(path);
   }

   public byte[] getData() {
      return headerData.toByteArray();
   }

   public String getMessageName() {
      return NAME.getNoLog();
   }

   public String toXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<SimpleHeaderInfo>");
      ByteUtil.printByteDump(builder, this.getData(), 0, HEADER_BYTE_SIZE, 8);
      builder.append("</SimpleHeaderInfo>");
      return builder.toString();
   }

   public Element[] getElements() {
      return new Element[]{NAME};
   }

   public int getHeaderSize() {
      return HEADER_BYTE_SIZE;
   }

   public void setNewBackingBuffer(byte[] data) {
      headerData.setNewBackingBuffer(data);
   }
}

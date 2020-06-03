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

package org.eclipse.osee.ote.remote.messages;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.eclipse.osee.ote.message.elements.ArrayElement;
import org.eclipse.osee.ote.message.event.OteEventMessage;


public class ConsoleMessage extends OteEventMessage {

   public static final int _BYTE_SIZE = 0;
   public ArrayElement STRINGDATA;

   public ConsoleMessage(String name, String topic) {
      super(ConsoleMessage.class.getSimpleName(), topic, _BYTE_SIZE);
      STRINGDATA = new ArrayElement(this, "STRINGDATA", getDefaultMessageData(), 0, 0, 0);
      addElements(STRINGDATA);
   }
   
   public void setString(String str) throws IOException{
      byte[] data = str.getBytes();
      int offset = STRINGDATA.getByteOffset() + getHeaderSize();
      byte[] newData = new byte[data.length + offset];
      System.arraycopy(getData(), 0, newData, 0, offset);
      System.arraycopy(data, 0, newData, offset, data.length);
      getDefaultMessageData().setNewBackingBuffer(newData);
   }
   
   public String getString() throws IOException, ClassNotFoundException{
      int offset = STRINGDATA.getByteOffset() + getHeaderSize();
      byte[] stringData = new byte[getData().length - offset];
      System.arraycopy(getData(), offset, stringData, 0, stringData.length);
      return new String(stringData);
   }
   
   public void setStringData(ByteBuffer buffer) {
      int offset = STRINGDATA.getByteOffset() + getHeaderSize();
      byte[] newData = new byte[buffer.remaining() + offset];
      System.arraycopy(getData(), 0, newData, 0, offset);
      buffer.get(newData, offset, buffer.remaining());
      getDefaultMessageData().setNewBackingBuffer(newData);
   }
}  

	

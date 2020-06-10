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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;

public class OteByteMessage extends Message{

   protected final OteByteMessageData data;
   
   public OteByteMessage(String name, String topic, int messageId, int defaultByteSize) {
      super(name, defaultByteSize, 0, false, 0, 0);
      data = new OteByteMessageData(this, topic, messageId, defaultByteSize);
      setDefaultMessageData(data);
      setMemSource(OteByteMessageType.OTE_BYTE_MESSAGE);
   }
   
   public OteByteMessage(String name, String topic, int defaultByteSize) {
      this(name, topic, 1, defaultByteSize);
   }
   
   public OteByteMessage(byte[] bytedata) {
	  super("holder", 0, 0, false, 0, 0);
	  data = new OteByteMessageData(this, bytedata.length);
      data.getMem().setData(bytedata);
	  setDefaultMessageData(data);
      setMemSource(OteByteMessageType.OTE_BYTE_MESSAGE);
   }

   public OteByteMessageHeader getHeader(){
	   return ((OteByteMessageData)getDefaultMessageData()).getMsgHeader();
   }
   
   protected byte[] serializeObject(Object obj) throws IOException{
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(obj);
      oos.flush();
      return bos.toByteArray();
   }
   
   protected ObjectInputStream unserializeObject(byte[] data, int offset, int length) throws IOException, ClassNotFoundException{
      ByteArrayInputStream bis = new ByteArrayInputStream(data, offset, length);
      return new ObjectInputStream(bis);
   }
   
   protected void setAndGrowData(byte[] newData, int startOffset){
      byte[] oldBytes = getDefaultMessageData().getMem().getData();
      byte[] newBytes = new byte[startOffset + newData.length];
      System.arraycopy(oldBytes, 0, newBytes, 0, startOffset);
      System.arraycopy(newData, 0, newBytes, startOffset, newData.length);
      getDefaultMessageData().getMem().setData(newBytes);
   }
   
}

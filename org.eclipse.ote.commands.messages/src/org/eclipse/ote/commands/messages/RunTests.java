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
package org.eclipse.ote.commands.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.ote.message.elements.ArrayElement;
import org.eclipse.ote.bytemessage.OteByteMessage;


public class RunTests extends OteByteMessage {
   
   public static final String TOPIC = "ote/server/runtests";
   
   private static final int DEFAULT_BYTE_SIZE = 32;
   
   public ArrayElement UUID_DATA_SESSION;
   public ArrayElement UUID_DATA_CMD_ID; 
   public ArrayElement PROPERTY_DATA;

   private IPropertyStore global;
   private List<IPropertyStore> scripts;
   private IPropertyStore[] properties;
   
   public RunTests(){
      super(RunTests.class.getSimpleName(), TOPIC, 0, DEFAULT_BYTE_SIZE);
      UUID_DATA_SESSION = new ArrayElement(this, "UUID_DATA_SESSION", getDefaultMessageData(), 0, 0, 127);
      UUID_DATA_CMD_ID = new ArrayElement(this, "UUID_DATA_CMD_ID", getDefaultMessageData(), 16, 0, 127);
      PROPERTY_DATA = new ArrayElement(this, "PROPERTY_DATA", getDefaultMessageData(), 32, 0, 0);
   }

   public void setSessionUUID(UUID userSessionId) {
      ByteBuffer buffer = UUID_DATA_SESSION.asByteBuffer();
      buffer.putLong(userSessionId.getMostSignificantBits());
      buffer.putLong(userSessionId.getLeastSignificantBits());
   }
   
   public UUID getSessionUUID(){
      ByteBuffer buffer = UUID_DATA_SESSION.asByteBuffer();
      return new UUID(buffer.getLong(), buffer.getLong());
   }
   
   public void setCmdUUID(UUID userSessionId) {
      ByteBuffer buffer = UUID_DATA_CMD_ID.asByteBuffer();
      buffer.putLong(userSessionId.getMostSignificantBits());
      buffer.putLong(userSessionId.getLeastSignificantBits());
   }
   
   public UUID getCmdUUID(){
      ByteBuffer buffer = UUID_DATA_CMD_ID.asByteBuffer();
      return new UUID(buffer.getLong(), buffer.getLong());
   }

   public void setScriptsAndGlobalConfig(IPropertyStore global, List<IPropertyStore> scripts) throws IOException {
      properties = null;
      List<IPropertyStore> properties = new ArrayList<IPropertyStore>();
      properties.add(global);
      properties.addAll(scripts);
      byte[] bytes = serializeObject(properties.toArray(new IPropertyStore[0]));
      setAndGrowData(bytes, getHeaderSize() + PROPERTY_DATA.getByteOffset());
   }
   
   public List<IPropertyStore> getScripts() throws IOException, ClassNotFoundException{
      if(scripts == null) { 
         getPropertyStoreData();
      }
      return scripts;
   }
   
   public IPropertyStore getGlobalConfig() throws IOException, ClassNotFoundException{
      if(global == null) { 
         getPropertyStoreData();
      }
      return global;
   }
   
   private IPropertyStore[] getPropertyStoreData() throws IOException, ClassNotFoundException{
      if(properties == null) { 
         byte[] data = getDefaultMessageData().getMem().getData();
         int offset = getHeaderSize() + PROPERTY_DATA.getByteOffset();
         ObjectInputStream ois = unserializeObject(data, offset, data.length - offset);
         Object obj = ois.readObject();
         if(obj instanceof IPropertyStore[]){
            properties = (IPropertyStore[])obj;
         }
         if(properties.length > 0){
            global = properties[0];
         }
         scripts = new ArrayList<IPropertyStore>();
         for(int i = 1; i < properties.length; i++){
            scripts.add(properties[i]);
         }
      }
      return properties;
   }
   
}

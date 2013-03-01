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
package org.eclipse.ote.connect.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.eclipse.osee.ote.core.environment.BundleDescription;
import org.eclipse.osee.ote.message.elements.ArrayElement;
import org.eclipse.ote.bytemessage.OteByteMessage;


public class ServerConfigurationRequest extends OteByteMessage {
   
   public static final String TOPIC = "ote/server/configurationrequest";
   
   private static final int DEFAULT_BYTE_SIZE = 16;
   
   public ArrayElement UUID_DATA; 
   public ArrayElement BUNDLE_DATA;

   private BundleDescription[] bundles;
   
   public ServerConfigurationRequest(){
      super(ServerConfigurationRequest.class.getSimpleName(), TOPIC, 0, DEFAULT_BYTE_SIZE);
      UUID_DATA = new ArrayElement(this, "UUID_DATA", getDefaultMessageData(), 0, 0, 127);
      BUNDLE_DATA = new ArrayElement(this, "BUNDLE_DATA", getDefaultMessageData(), 16, 0, 0);
   }

   public void setSessionUUID(UUID userSessionId) {
      ByteBuffer buffer = UUID_DATA.asByteBuffer();
      buffer.putLong(userSessionId.getMostSignificantBits());
      buffer.putLong(userSessionId.getLeastSignificantBits());
   }
   
   public UUID getSessionUUID(){
      ByteBuffer buffer = UUID_DATA.asByteBuffer();
      return new UUID(buffer.getLong(), buffer.getLong());
   }

   public void setBundleConfiguration(BundleDescription[] bundles) throws IOException {
      byte[] bundleBytes = serializeObject(bundles);
      setAndGrowData(bundleBytes, getHeaderSize() + BUNDLE_DATA.getByteOffset());
   }
   
   public BundleDescription[] getBundleConfiguration() throws IOException, ClassNotFoundException{
      if(bundles == null) { 
         byte[] data = getDefaultMessageData().getMem().getData();
         int offset = getHeaderSize() + BUNDLE_DATA.getByteOffset();
         ObjectInputStream ois = unserializeObject(data, offset, data.length - offset);
         Object obj = ois.readObject();
         if(obj instanceof BundleDescription[]){
            bundles = (BundleDescription[])obj;
         }
      }
      return bundles;
   }
   
}

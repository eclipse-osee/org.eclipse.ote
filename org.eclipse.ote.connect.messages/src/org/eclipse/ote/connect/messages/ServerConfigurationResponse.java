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

import java.nio.ByteBuffer;
import java.util.UUID;

import org.eclipse.osee.ote.message.elements.ArrayElement;
import org.eclipse.osee.ote.message.elements.EnumeratedElement;
import org.eclipse.ote.bytemessage.OteByteMessage;


public class ServerConfigurationResponse extends OteByteMessage {
   
   public static final String TOPIC = "ote/server/configurationresponse";
   
   private static final int DEFAULT_BYTE_SIZE = 17;
   
   public ArrayElement UUID_DATA; 
   public EnumeratedElement<RequestStatus> STATUS;

   public ServerConfigurationResponse(){
      super(ServerConfigurationResponse.class.getSimpleName(), TOPIC, 0, DEFAULT_BYTE_SIZE);
      getHeader().RESPONSE_TOPIC.setValue(ServerSessionResponse.TOPIC);
      
      UUID_DATA = new ArrayElement(this, "UUID_DATA", getDefaultMessageData(), 0, 0, 127);
      STATUS = new EnumeratedElement<RequestStatus>(this, "STATUS", RequestStatus.class, getDefaultMessageData(), 16, 0, 7);
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

}

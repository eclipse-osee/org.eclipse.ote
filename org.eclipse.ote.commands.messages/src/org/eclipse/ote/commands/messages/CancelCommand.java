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

import java.nio.ByteBuffer;
import java.util.UUID;

import org.eclipse.osee.ote.message.elements.ArrayElement;
import org.eclipse.ote.bytemessage.OteByteMessage;


public class CancelCommand extends OteByteMessage {
   
   public static final String TOPIC = "ote/server/cancelcommand";
   
   private static final int DEFAULT_BYTE_SIZE = 32;
   
   public ArrayElement UUID_DATA_SESSION;
   public ArrayElement UUID_DATA_CMD_ID; 
   
   
   public CancelCommand(){
      super(CancelCommand.class.getSimpleName(), TOPIC, 0, DEFAULT_BYTE_SIZE);
      UUID_DATA_SESSION = new ArrayElement(this, "UUID_DATA_SESSION", getDefaultMessageData(), 0, 0, 127);
      UUID_DATA_CMD_ID = new ArrayElement(this, "UUID_DATA_CMD_ID", getDefaultMessageData(), 16, 0, 127);
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

}

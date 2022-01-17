/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.client.msg.core.internal;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.tool.MessageMode;

/**
 * @author Ken J. Aguilar
 */
public class MessageReference {

   private final DataType type;
   private final MessageMode mode;
   private final String msgClassName;

   public MessageReference(DataType type, MessageMode mode, String msgClassName) {
      this.type = type;
      this.mode = mode;
      this.msgClassName = msgClassName;
   }

   public MessageReference(Message msg) {
      this.type = msg.getDefaultMessageData().getPhysicalIoType();
      this.mode = MessageMode.READER;
      this.msgClassName = msg.getMessageName();
   }

   public String getMsgClass() {
      return msgClassName;
   }

   public DataType getType() {
      return type;
   }

   public MessageMode getMode() {
      return mode;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      MessageReference otherRef = (MessageReference) obj;
      return msgClassName.equals(otherRef.msgClassName) && type == otherRef.type && mode == otherRef.mode;
   }

   @Override
   public int hashCode() {
      return msgClassName.hashCode() ^ mode.hashCode() ^ type.hashCode();
   }

}

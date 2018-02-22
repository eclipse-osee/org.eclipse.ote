/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message.commands;

import java.io.Serializable;
import java.net.InetSocketAddress;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.tool.MessageMode;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class UnSubscribeToMessage implements Serializable {

   private static final long serialVersionUID = -1140091630056507142L;
   private final String messageName;
   private final MessageMode mode;
   private final InetSocketAddress address;
   private final DataType memTypeOrdinal;

   /**
    * Creates a new unsubscribe command.
    * 
    * @param messageName the name of the message that the message manager service will no longer sent updates to for the
    * specified client address
    * @param address the address of the client.
    */
   public UnSubscribeToMessage(final String messageName, final MessageMode mode, final DataType memTypeOrdinal, InetSocketAddress address) {
      this.messageName = messageName;
      this.mode = mode;
      this.address = address;
      this.memTypeOrdinal = memTypeOrdinal;
   }

   public String getMessage() {
      return messageName;
   }

   public InetSocketAddress getAddress() {
      return address;
   }

   public DataType getMemTypeOrdinal() {
      return memTypeOrdinal;
   }

   /**
    * @return Returns the mode.
    */
   public MessageMode getMode() {
      return mode;
   }

}

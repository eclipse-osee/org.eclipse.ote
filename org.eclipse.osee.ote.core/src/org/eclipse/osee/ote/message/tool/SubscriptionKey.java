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

package org.eclipse.osee.ote.message.tool;

import java.io.Serializable;
import org.eclipse.osee.ote.message.enums.DataType;

/**
 * @author Ken J. Aguilar
 */
public final class SubscriptionKey implements Serializable {

   private static final long serialVersionUID = 4385205425559852952L;

   private final int id;
   private final DataType type;
   private final MessageMode mode;
   private final String messageClassName;

   public SubscriptionKey(int id, DataType type, MessageMode mode, String messageClassName) {
      this.id = id;
      this.type = type;
      this.mode = mode;
      this.messageClassName = messageClassName;
   }

   public int getId() {
      return id;
   }

   public DataType getType() {
      return type;
   }

   public MessageMode getMode() {
      return mode;
   }

   public String getMessageClassName() {
      return messageClassName;
   }

}

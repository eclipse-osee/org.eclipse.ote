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
package org.eclipse.ote.ui.message;
import java.util.List;

import org.eclipse.ote.message.lookup.MessageInput;
import org.eclipse.ote.message.lookup.MessageInputItem;


public class MessageInputComponent implements MessageInput {

   @Override
   public String getLabel() {
      return "Message Watch";
   }

   @Override
   public void add(List<MessageInputItem> items) {
      MessageInputUtil.add(items, false);
   }

   @Override
   public boolean messagesOnly() {
      return false;
   }

}

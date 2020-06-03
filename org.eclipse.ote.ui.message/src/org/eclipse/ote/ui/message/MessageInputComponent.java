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

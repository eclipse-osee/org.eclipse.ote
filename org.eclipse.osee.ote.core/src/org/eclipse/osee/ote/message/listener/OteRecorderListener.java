/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ote.message.listener;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;

/**
 * @author Shandeep Singh
 */
public class OteRecorderListener implements IOSEEMessageListener {

   private final WeakReference<Message> message;
   private List<Message> messageRecordings;
   private int maxMessageRecordings;

   public OteRecorderListener(Message msg, List<Message> messageRecordings, int maxMessageRecordings) {
      super();
      this.message = new WeakReference<>(msg);
      this.messageRecordings = messageRecordings;
      this.maxMessageRecordings = maxMessageRecordings;
   }

   @Override
   public void onDataAvailable(MessageData data, DataType type) throws MessageSystemException {
      Message copiedMessage = copyMessage(data);
      if (messageRecordings.size() < maxMessageRecordings) {
         messageRecordings.add(copiedMessage);
      }
   }

   private Message copyMessage(MessageData data) {
      Message originalMessage = message.get();
      Message copiedMessage = null;
      try {
         Class<?> clazz = originalMessage.getClass();
         copiedMessage = (Message) clazz.newInstance();
         copiedMessage.setData(data.toByteArray());
      } catch (Exception e) {
         OseeLog.log(OteRecorderListener.class, Level.SEVERE, "Failed to copy OTE Recording Message");
      }
      return copiedMessage;
   }

   @Override
   public void onInitListener() throws MessageSystemException {
      // INTENTIONALLY EMPTY
   }

   public void setMessageRecordings(List<Message> messageRecordings) {
      this.messageRecordings = messageRecordings;
   }

}

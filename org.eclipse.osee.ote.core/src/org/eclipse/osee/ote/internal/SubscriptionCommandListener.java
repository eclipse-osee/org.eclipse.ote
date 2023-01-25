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
package org.eclipse.osee.ote.internal;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.commands.SubscribeToMessage;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.eclipse.osee.ote.message.tool.SubscriptionDetails;
import org.eclipse.osee.ote.remote.messages.SerializedSubscribeToMessage;
import org.eclipse.osee.ote.remote.messages.SerializedSubscriptionDetailsMessage;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class SubscriptionCommandListener implements EventHandler {

   private final IRemoteMessageService messageService;
   private final SerializedSubscribeToMessage cmd;
   
   public SubscriptionCommandListener(EventAdmin eventAdmin, IRemoteMessageService messageService) {
      this.messageService = messageService;
      cmd = new SerializedSubscribeToMessage();
   }

   @Override
   public void handleEvent(Event event) {
      OteEventMessageUtil.putBytes(event, cmd);
      SubscribeToMessage subscribe;
      try {
         subscribe = cmd.getObject();
         SubscriptionDetails resp = messageService.subscribeToMessage(subscribe);
         SerializedSubscriptionDetailsMessage stat = new SerializedSubscriptionDetailsMessage();
         stat.getHeader().RESPONSE_ID.setValue(cmd.getHeader().MESSAGE_SEQUENCE_NUMBER.getValue());
         stat.setObject(resp);
         OteEventMessageUtil.postEvent(stat);
      } catch (UnknownHostException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      } catch (IOException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      } catch (ClassNotFoundException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
   }

}

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
import org.eclipse.osee.ote.message.commands.UnSubscribeToMessage;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.eclipse.osee.ote.remote.messages.SerializedUnSubscribeMessage;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class UnSubscribeCommandListener implements EventHandler {

   private final IRemoteMessageService messageService;
   private final SerializedUnSubscribeMessage cmd;
   
   public UnSubscribeCommandListener(EventAdmin eventAdmin, IRemoteMessageService messageService) {
      this.messageService = messageService;
      cmd = new SerializedUnSubscribeMessage();
   }

   @Override
   public void handleEvent(Event event) {
      OteEventMessageUtil.putBytes(event, cmd);
      UnSubscribeToMessage unsubscribe;
      try {
         unsubscribe = cmd.getObject();
         messageService.unsubscribeToMessage(unsubscribe);
      } catch (UnknownHostException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      } catch (IOException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      } catch (ClassNotFoundException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
   }

}

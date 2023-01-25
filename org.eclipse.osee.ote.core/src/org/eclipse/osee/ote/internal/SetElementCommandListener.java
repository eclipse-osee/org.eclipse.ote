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
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.commands.SetElementValue;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.eclipse.osee.ote.remote.messages.SerializedSetElementMessage;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class SetElementCommandListener implements EventHandler {

   private final IRemoteMessageService messageService;

   public SetElementCommandListener(EventAdmin eventAdmin, IRemoteMessageService messageService) {
      this.messageService = messageService;
   }

   @Override
   public void handleEvent(Event arg0) {
      SetElementValue cmd;
      try {
         SerializedSetElementMessage msg = new SerializedSetElementMessage(OteEventMessageUtil.getBytes(arg0));
         cmd = msg.getObject();
         messageService.setElementValue(cmd);
      } catch (IOException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      } catch (ClassNotFoundException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
   }

}

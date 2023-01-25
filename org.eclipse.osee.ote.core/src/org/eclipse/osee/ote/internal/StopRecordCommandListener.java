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

import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class StopRecordCommandListener implements EventHandler {

   private final IRemoteMessageService messageService;
   
   public StopRecordCommandListener(EventAdmin eventAdmin, IRemoteMessageService messageService) {
      this.messageService = messageService;
   }

   @Override
   public void handleEvent(Event arg0) {
      messageService.stopRecording();
   }

}

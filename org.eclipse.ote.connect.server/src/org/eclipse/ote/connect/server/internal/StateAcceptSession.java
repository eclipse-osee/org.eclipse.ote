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
package org.eclipse.ote.connect.server.internal;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.ote.bytemessage.OteSendByteMessage;
import org.eclipse.ote.connect.messages.RequestStatus;
import org.eclipse.ote.connect.messages.ServerSessionRequest;
import org.eclipse.ote.connect.messages.ServerSessionResponse;
import org.eclipse.ote.statemachine.BaseInput;
import org.eclipse.ote.statemachine.BaseState;
import org.eclipse.ote.statemachine.StateMachine;

public class StateAcceptSession extends BaseState {

   private OteSendByteMessage sender;
   private IHostTestEnvironment host;

   public StateAcceptSession(StateMachine sm, OteSendByteMessage sender, IRuntimeLibraryManager runtimeLibraryManager, IHostTestEnvironment host) throws Exception {
      this.sender = sender;
      this.host = host;
   }

   @Override
   public void run(BaseInput input) {
      if(InputServerSessionRequest.TYPE == input.getType()){
         InputServerSessionRequest inputServerSessionRequest = (InputServerSessionRequest)input;
         ServerSessionRequest request = inputServerSessionRequest.get();
         ServerSessionResponse response = new ServerSessionResponse();
         response.setSessionUUID(request.getSessionUUID());
         response.STATUS.setValue(RequestStatus.yes);
         try{
            host.requestEnvironment(new ServerSideRemoteUserSession(request.getSessionUUID(), sender), null);
         } catch (Throwable ex){
            response.STATUS.setValue(RequestStatus.no);
            OseeLog.log(getClass(), Level.SEVERE, "Failed to start test environment", ex);
         }
         sender.asynchSend(response);
      }
   }

   @Override
   public void entry() {
   }

}

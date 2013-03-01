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

import org.eclipse.ote.bytemessage.OteSendByteMessage;
import org.eclipse.ote.connect.messages.RequestStatus;
import org.eclipse.ote.connect.messages.ServerSessionRequest;
import org.eclipse.ote.connect.messages.ServerSessionResponse;
import org.eclipse.ote.statemachine.BaseInput;
import org.eclipse.ote.statemachine.BaseState;

public class StateRejectSession extends BaseState {

   private OteSendByteMessage sender;

   public StateRejectSession(OteSendByteMessage sender){
      this.sender = sender;
   }
   
   @Override
   public void run(BaseInput input) {
      if(InputServerSessionRequest.TYPE == input.getType()){
         InputServerSessionRequest inputServerSessionRequest = (InputServerSessionRequest)input;
         ServerSessionRequest request = inputServerSessionRequest.get();
         ServerSessionResponse response = new ServerSessionResponse();
         response.setSessionUUID(request.getSessionUUID());
         response.STATUS.setValue(RequestStatus.no);
         sender.asynchSend(response);
      }
   }

   @Override
   public void entry() {
   }

}

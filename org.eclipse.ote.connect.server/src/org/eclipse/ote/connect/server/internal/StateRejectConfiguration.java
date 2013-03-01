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
import org.eclipse.ote.connect.messages.ServerConfigurationResponse;
import org.eclipse.ote.statemachine.BaseInput;
import org.eclipse.ote.statemachine.BaseState;

public class StateRejectConfiguration extends BaseState {

   private OteSendByteMessage sender;
   
   public StateRejectConfiguration(OteSendByteMessage sender) {
      this.sender = sender;
   }

   @Override
   public void run(BaseInput input) {
      if(input.getType() == InputServerConfigurationRequest.TYPE){
         try{
            ServerConfigurationResponse response = new ServerConfigurationResponse();
            InputServerConfigurationRequest request = (InputServerConfigurationRequest)input;
            response.setSessionUUID(request.get().getSessionUUID());
            response.STATUS.setValue(RequestStatus.no);
            sender.asynchSend(response);
         } catch (Exception ex){
            ex.printStackTrace();
         }
      }
   }

   @Override
   public void entry() {
   }

}

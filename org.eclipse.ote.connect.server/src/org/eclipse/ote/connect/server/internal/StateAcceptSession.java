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

import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.ote.bytemessage.OteByteMessageResponseFuture;
import org.eclipse.ote.bytemessage.OteSendByteMessage;
import org.eclipse.ote.connect.messages.RequestStatus;
import org.eclipse.ote.connect.messages.ServerConfigurationRequest;
import org.eclipse.ote.connect.messages.ServerSessionRequest;
import org.eclipse.ote.connect.messages.ServerSessionResponse;
import org.eclipse.ote.statemachine.BaseInput;
import org.eclipse.ote.statemachine.ChildStateMachineState;
import org.eclipse.ote.statemachine.StateMachine;

public class StateAcceptSession extends ChildStateMachineState {

   private OteSendByteMessage sender;
   private OteByteMessageResponseFuture<ServerConfigurationRequest> serverConfigurationRequestFuture;

   public StateAcceptSession(StateMachine sm, OteSendByteMessage sender, IRuntimeLibraryManager runtimeLibraryManager) throws Exception {
      super(sm);
      this.sender = sender;
      
      InputServerConfigurationRequest inputServerConfigurationRequest = new InputServerConfigurationRequest(sm);
      InputStartingConfiguration inputStartingConfiguration = new InputStartingConfiguration(sm);
      InputAcceptingUpdatedConfiguration inputAcceptingUpdatedConfiguration = new InputAcceptingUpdatedConfiguration(sm);
      
      serverConfigurationRequestFuture = sender.asynchResponse(ServerConfigurationRequest.class, ServerConfigurationRequest.TOPIC, new ServerSessionConfigurationHandler(inputServerConfigurationRequest));
      
      StateAcceptConfiguration stateAcceptConfiguration = new StateAcceptConfiguration(sender, inputAcceptingUpdatedConfiguration, inputStartingConfiguration, runtimeLibraryManager);
      StateRejectConfiguration stateRejectConfiguration = new StateRejectConfiguration(sender);
      
      setDefaultInitialState(stateAcceptConfiguration);
      newTransition(stateAcceptConfiguration, inputServerConfigurationRequest, stateAcceptConfiguration);
      newTransition(stateAcceptConfiguration, inputStartingConfiguration, stateRejectConfiguration);
      newTransition(stateRejectConfiguration, inputServerConfigurationRequest, stateRejectConfiguration);
      newTransition(stateRejectConfiguration, inputAcceptingUpdatedConfiguration, stateAcceptConfiguration);
      
   }

   @Override
   public void exit(){
      super.exit();
      serverConfigurationRequestFuture.cancel();
   }

   @Override
   public void preRunStateMachine(BaseInput input) {
      if(InputServerSessionRequest.TYPE == input.getType()){
         InputServerSessionRequest inputServerSessionRequest = (InputServerSessionRequest)input;
         ServerSessionRequest request = inputServerSessionRequest.get();
         ServerSessionResponse response = new ServerSessionResponse();
         response.setSessionUUID(request.getSessionUUID());
         response.STATUS.setValue(RequestStatus.yes);
         sender.asynchSend(response);
      }
   }

   @Override
   public void postRunStateMachine(BaseInput input) {
      
   }

}

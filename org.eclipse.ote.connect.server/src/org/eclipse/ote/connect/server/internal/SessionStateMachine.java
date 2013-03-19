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
import org.eclipse.ote.bytemessage.OteByteMessageResponseFuture;
import org.eclipse.ote.bytemessage.OteSendByteMessage;
import org.eclipse.ote.connect.messages.ServerSessionRequest;
import org.eclipse.ote.services.core.ServiceUtility;
import org.eclipse.ote.statemachine.StateMachine;
import org.osgi.service.event.EventAdmin;

public class SessionStateMachine {
   private IRuntimeLibraryManager runtimeLibraryManager;
   StateMachine sm;
   private OteByteMessageResponseFuture<ServerSessionRequest> serverSessionRequestFuture;
   private IHostTestEnvironment host;

   
   public SessionStateMachine( IRuntimeLibraryManager runtimeLibraryManager, IHostTestEnvironment host){
      this.runtimeLibraryManager = runtimeLibraryManager;
      this.host = host;
   }
   
   public void start(){
      try{
         sm = new StateMachine("SessionStateMachine");

         OteSendByteMessage sender = new OteSendByteMessage(ServiceUtility.getService(EventAdmin.class));
         
         InputServerSessionRequest inputServerSesisonRequest = new InputServerSessionRequest(sm);
         InputNoMoreSessions inputNoMoreSessions = new InputNoMoreSessions(sm);
         InputAllowMoreSessions inputAllowMoreSessions = new InputAllowMoreSessions(sm);
         
         serverSessionRequestFuture = 
               sender.asynchResponse(ServerSessionRequest.class, ServerSessionRequest.TOPIC, new ServerSessionRequestHandler(inputServerSesisonRequest));
         
         StateAcceptSession stateAcceptSession = new StateAcceptSession(sm, sender, runtimeLibraryManager, host);
         StateRejectSession stateRejectSession = new StateRejectSession(sender);

         sm.setDefaultInitialState(stateAcceptSession);
         sm.newTransition(stateAcceptSession, inputServerSesisonRequest, stateAcceptSession);
         sm.newTransition(stateAcceptSession, inputNoMoreSessions, stateRejectSession);
         sm.newTransition(stateRejectSession, inputAllowMoreSessions, stateAcceptSession);
         
         sm.initialize();
         
         sm.start();
      } catch (Exception ex){
         OseeLog.log(getClass(), Level.SEVERE, ex);
      }
   }
   
   public void stop(){
      if(serverSessionRequestFuture != null){
         serverSessionRequestFuture.cancel();
      }
   }
}

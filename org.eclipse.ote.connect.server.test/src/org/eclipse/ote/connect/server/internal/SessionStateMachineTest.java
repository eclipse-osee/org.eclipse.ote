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

import java.io.IOException;
import java.util.UUID;

import junit.framework.Assert;

import org.eclipse.ote.bytemessage.OteSendByteMessage;
import org.eclipse.ote.connect.messages.RequestStatus;
import org.eclipse.ote.connect.messages.ServerSessionRequest;
import org.eclipse.ote.connect.messages.ServerSessionResponse;
import org.eclipse.ote.services.core.ServiceUtility;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.event.EventAdmin;

public class SessionStateMachineTest {

   @Before
   public void setup(){
      for(Bundle bundle:ServiceUtility.getContext().getBundles()){
         try {
            bundle.start();
         } catch (BundleException e) {
         }
      }
   }
   
   @Test
   public void testStateTransitions() throws InterruptedException, IOException {

      OteSendByteMessage sender = new OteSendByteMessage(ServiceUtility.getService(EventAdmin.class));
      UUID id = UUID.randomUUID();
      
      SessionStateMachine stateMachine = new SessionStateMachine(null);
      stateMachine.start();
    
      ServerSessionRequest request = new ServerSessionRequest();
      request.setSessionUUID(id);
      
      Object obj = stateMachine.sm.getCurrentState();
      Assert.assertEquals(StateAcceptSession.class, obj.getClass());

      SessionResponse sessionResponse = new SessionResponse();
      sender.asynchResponse(ServerSessionResponse.class, ServerSessionResponse.TOPIC, sessionResponse);
      sender.asynchSend(request);
      
      Thread.sleep(500);
      
      Assert.assertNotNull(sessionResponse.received);
      Assert.assertEquals(RequestStatus.no, sessionResponse.received.STATUS.getValue());
      
      Assert.assertEquals(StateAcceptSession.class, obj.getClass());
   }
   
}

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
import java.net.URL;
import java.util.UUID;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import junit.framework.Assert;

import org.eclipse.osee.ote.core.environment.BundleDescription;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.ote.bytemessage.OteSendByteMessage;
import org.eclipse.ote.connect.messages.OpMode;
import org.eclipse.ote.connect.messages.RequestStatus;
import org.eclipse.ote.connect.messages.ServerConfigurationRequest;
import org.eclipse.ote.connect.messages.ServerConfigurationResponse;
import org.eclipse.ote.connect.messages.ServerConfigurationStatus;
import org.eclipse.ote.connect.messages.ServerSessionRequest;
import org.eclipse.ote.connect.messages.ServerSessionResponse;
import org.eclipse.ote.services.core.ServiceUtility;
import org.eclipse.ote.statemachine.ChildStateMachineState;
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
      IRuntimeLibraryManager runtimeLibManager = ServiceUtility.getService(IRuntimeLibraryManager.class);
      Assert.assertNotNull(runtimeLibManager);
      
      SessionStateMachine stateMachine = new SessionStateMachine(runtimeLibManager);
      stateMachine.start();
    
      ServerSessionRequest request = new ServerSessionRequest();
      request.setSessionUUID(id);
      
      Object obj = stateMachine.sm.getCurrentState();
      Assert.assertEquals(StateAcceptSession.class, obj.getClass());
      Assert.assertTrue(obj instanceof ChildStateMachineState);

      SessionResponse sessionResponse = new SessionResponse();
      sender.asynchResponse(ServerSessionResponse.class, ServerSessionResponse.TOPIC, sessionResponse);
      sender.asynchSend(request);
      
      Thread.sleep(500);
      
      Assert.assertNotNull(sessionResponse.received);
      Assert.assertEquals(RequestStatus.yes, sessionResponse.received.STATUS.getValue());
      
      Assert.assertEquals(StateAcceptSession.class, obj.getClass());
      StateAcceptSession stateAcceptSession = (StateAcceptSession)obj;
      obj = stateAcceptSession.getCurrentState();
      Assert.assertEquals(StateAcceptConfiguration.class, obj.getClass());
      
      ServerConfigurationRequest configRequest = generateConfigRequest(id);
      
      ConfigStatus configStatus = new ConfigStatus();
      ConfigResponse configResponse = new ConfigResponse();
      sender.asynchResponse(ServerConfigurationStatus.class, ServerConfigurationStatus.TOPIC, configStatus);
      sender.asynchResponse(ServerConfigurationResponse.class, ServerConfigurationResponse.TOPIC, configResponse);
      sender.asynchSend(configRequest);
      Thread.sleep(2000);
      Assert.assertNotNull(configResponse.received);
      Assert.assertEquals(RequestStatus.yes, configResponse.received.STATUS.getValue());
      Assert.assertNotNull(configStatus.received);
      Assert.assertEquals(OpMode.fail, configStatus.received.STATUS.getValue());
      
      obj = stateAcceptSession.getCurrentState();
      Assert.assertEquals(StateRejectConfiguration.class, obj.getClass());
      
      sender.asynchSend(configRequest);
      Thread.sleep(1000);
      Assert.assertNotNull(configResponse.received);
      Assert.assertEquals(RequestStatus.no, configResponse.received.STATUS.getValue());
   }
   
   private ServerConfigurationRequest generateConfigRequest(UUID id) throws IOException{
      ServerConfigurationRequest configRequest = new ServerConfigurationRequest();
      configRequest.setSessionUUID(id);
      BundleDescription[] bundles = new BundleDescription[1];
      Manifest manifest = new Manifest();
      manifest.getMainAttributes().putValue(Attributes.Name.MANIFEST_VERSION.toString(), "1.0");
      manifest.getMainAttributes().putValue("Bundle-SymbolicName", "ote.test");
      manifest.getMainAttributes().putValue("Bundle-Version", "1.0.0");
      bundles[0] = new BundleDescription(new URL("http://localhost/bundle"), new URL("http://localhost/server"), manifest, false, new byte[8]);
      configRequest.setBundleConfiguration(bundles);
      return configRequest;
   }

}

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
package org.eclipse.ote.connect.messages;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.jar.Manifest;

import junit.framework.Assert;

import org.eclipse.osee.ote.core.environment.BundleDescription;
import org.eclipse.ote.bytemessage.OteByteMessageUtil;
import org.eclipse.ote.bytemessage.OteSendByteMessage;
import org.eclipse.ote.services.core.ServiceUtility;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;


public class ServerSessionRequestTest {

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
   public void testUUID() throws IOException, ClassNotFoundException {
      ServerConfigurationRequest request = new ServerConfigurationRequest();
      UUID uuid = UUID.randomUUID();
      request.setSessionUUID(uuid);
      UUID returnedUUID = request.getSessionUUID();
      Assert.assertEquals(uuid, returnedUUID);
      
      BundleDescription[] descs = new BundleDescription[126];
      for(int i = 0; i < descs.length; i++){
         descs[i] = new BundleDescription(new URL("http://www.google.com"),new URL("http://www.google.com"), new Manifest(), false, new byte[8]);
      }
      request.setBundleConfiguration(descs);
      
      BundleDescription[] newdescs = request.getBundleConfiguration();
      
      Assert.assertEquals(descs.length, newdescs.length);
      Assert.assertEquals(descs[1].getMd5Digest().length, newdescs[1].getMd5Digest().length);
      Assert.assertEquals(descs[1].getLocation(), newdescs[1].getLocation());
   }

   @Test
   public void testResponse() throws Exception {
      ServerConfigurationResponse response = new ServerConfigurationResponse();
      response.STATUS.setValue(RequestStatus.yes);
      RespondAfterWait respondAfterWait = new RespondAfterWait(response, 30);
      ServiceRegistration<EventHandler> reg = OteByteMessageUtil.subscribe(ServerConfigurationRequest.TOPIC, respondAfterWait);
      try{
         OteSendByteMessage sender = new OteSendByteMessage(ServiceUtility.getService(EventAdmin.class));
         ServerConfigurationRequest request = new ServerConfigurationRequest();
         request.getHeader().RESPONSE_TOPIC.setValue(ServerConfigurationResponse.TOPIC);
         UUID uuid = UUID.randomUUID();
         request.setSessionUUID(uuid);

         BundleDescription[] descs = new BundleDescription[126];
         for(int i = 0; i < descs.length; i++){
            descs[i] = new BundleDescription(new URL("http://www.google.com"),new URL("http://www.google.com"), new Manifest(), false, new byte[8]);
         }
         request.setBundleConfiguration(descs);

         ServerConfigurationResponse result = sender.synchSendAndResponse(ServerConfigurationResponse.class, request, 200);
         
         Assert.assertNotNull(result);
         Assert.assertEquals(result.STATUS.getValue(), RequestStatus.yes);
      }finally {
         reg.unregister();
         respondAfterWait.cancel();
      }
   }
}

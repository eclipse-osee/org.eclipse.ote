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
package org.eclipse.ote.bytemessage;

import org.eclipse.ote.services.core.ServiceUtility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class OteByteMessageSceneriosTest {

   private OteSendByteMessage sender;
   
   @Before
   public void setup(){
      for(Bundle bundle:ServiceUtility.getContext().getBundles()){
         try {
            bundle.start();
         } catch (BundleException e) {
         }
      }
      EventAdmin eventAdmin = ServiceUtility.getService(EventAdmin.class);
      sender = new OteSendByteMessage(eventAdmin);
   }
   
   @Test
   public void testSynchSendNoResponse() throws Exception {
      OteByteMessage msg = new SampleNoResponse();
      OteByteMessageResponse response;
      try{
         response = sender.synchSendAndResponse(OteByteMessageResponse.class, msg, 0);
         Assert.assertTrue("failed to throw exception when no response topic specified", false);
      } catch (Exception ex){
      }
      long current = System.currentTimeMillis();
      msg.getHeader().RESPONSE_TOPIC.setValue("nothingtoseehere");
      response = sender.synchSendAndResponse(OteByteMessageResponse.class, msg, 100);
      System.out.println((System.currentTimeMillis() - current));
      Assert.assertTrue((System.currentTimeMillis() - current) >= 95);
      Assert.assertNull(response);
   }
   
   @Test
   public void testSynchSendResponse() throws Exception {
      ServiceRegistration<EventHandler> reg = OteByteMessageUtil.subscribe(OteByteMessageSendMsg.TOPIC_VALUE, new RespondAfterWait(new OteByteMessageResponse(), 100));
      try{
         OteByteMessageSendMsg msg = new OteByteMessageSendMsg();
         msg.getHeader().RESPONSE_TOPIC.setValue(OteByteMessageResponse.TOPIC_VALUE);
         msg.getHeader().MESSAGE_SEQUENCE_NUMBER.setValue(665);


         long current = System.currentTimeMillis();
         OteByteMessageResponse response = sender.synchSendAndResponse(OteByteMessageResponse.class, msg, 150);
         Assert.assertNotNull(response);
         long responseTime = System.currentTimeMillis() - current;
         System.out.println(responseTime);
         Assert.assertEquals(666, response.getHeader().RESPONSE_ID.getValue().intValue());
         Assert.assertEquals("ote/response", response.getHeader().TOPIC.getValue());
         Assert.assertTrue(responseTime > 95 && responseTime < 200);
      } finally {
         reg.unregister();
      }
   }
   
   @Test
   public void testSynchSendResponseTimeout() throws Exception {
      RespondAfterWait respondAfterWait = new RespondAfterWait(new OteByteMessageResponse(), 300);
      ServiceRegistration<EventHandler> reg = OteByteMessageUtil.subscribe(OteByteMessageSendMsg.TOPIC_VALUE, respondAfterWait);
      try{
         OteByteMessageSendMsg msg = new OteByteMessageSendMsg();
         msg.getHeader().RESPONSE_TOPIC.setValue(OteByteMessageResponse.TOPIC_VALUE);
         msg.getHeader().MESSAGE_SEQUENCE_NUMBER.setValue(665);
         long current = System.currentTimeMillis();
         OteByteMessageResponse response = sender.synchSendAndResponse(OteByteMessageResponse.class, msg, 150);
         Assert.assertNull(response);
         long responseTime = System.currentTimeMillis() - current;
         System.out.println(responseTime);
         Assert.assertTrue(responseTime > 150 && responseTime < 200);

      } finally {
         reg.unregister();
         respondAfterWait.cancel();
      }
   }
   
   @Test
   public void testAsynchSendNoResponse() {
      RespondAfterWait respondAfterWait = new RespondAfterWait(new OteByteMessageResponse(), 300);
      ServiceRegistration<EventHandler> reg = OteByteMessageUtil.subscribe(OteByteMessageSendMsg.TOPIC_VALUE, respondAfterWait);
      try{
         OteByteMessageSendMsg msg = new OteByteMessageSendMsg();
         msg.getHeader().RESPONSE_TOPIC.setValue(OteByteMessageResponse.TOPIC_VALUE);
         msg.getHeader().MESSAGE_SEQUENCE_NUMBER.setValue(12);
         OteByteMessageCallable<OteByteMessageSendMsg, OteByteMessageResponse> callable = new OteByteMessageCallable<OteByteMessageSendMsg, OteByteMessageResponse>() {
            @Override
            public void timeout(OteByteMessageSendMsg transmitted) {
               Assert.assertTrue(true);
               System.out.println("got a good response 1");
            }
            @Override
            public void call(OteByteMessageSendMsg transmitted, OteByteMessageResponse recieved) {
               System.out.println("I should have failed");
               Assert.assertTrue("should have timed out", false);
            }
         };

         long time = System.currentTimeMillis();
         OteByteMessageFuture<OteByteMessageSendMsg, OteByteMessageResponse> result = sender.asynchSendAndResponse(OteByteMessageResponse.class, msg, callable, 100);
         result.waitForCompletion();
         long elapsed = System.currentTimeMillis() - time;
         System.out.println(elapsed);
         Assert.assertTrue(!result.gotResponse());
         Assert.assertTrue(result.isTimedOut());
      }finally {
         reg.unregister();
         respondAfterWait.cancel();
      }
   }
   
   
   @Test
   public void testAsynchSendResponse() {
      ServiceRegistration<EventHandler> reg = OteByteMessageUtil.subscribe(OteByteMessageSendMsg.TOPIC_VALUE, new RespondAfterWait(new OteByteMessageResponse(), 30));
      try{
         OteByteMessageSendMsg msg = new OteByteMessageSendMsg();
         msg.getHeader().RESPONSE_TOPIC.setValue(OteByteMessageResponse.TOPIC_VALUE);
         msg.getHeader().MESSAGE_SEQUENCE_NUMBER.setValue(12);
         OteByteMessageCallable<OteByteMessageSendMsg, OteByteMessageResponse> callable = new OteByteMessageCallable<OteByteMessageSendMsg, OteByteMessageResponse>() {
            @Override
            public void timeout(OteByteMessageSendMsg transmitted) {
               Assert.fail("should not have timed out");
            }
            @Override
            public void call(OteByteMessageSendMsg transmitted, OteByteMessageResponse recieved) {
               Assert.assertNotNull(recieved);
               Assert.assertEquals(13, recieved.getHeader().RESPONSE_ID.getValue().intValue());
               System.out.println("got a good response 222");
            }
         };

         long time = System.currentTimeMillis();
         OteByteMessageFuture<OteByteMessageSendMsg, OteByteMessageResponse> result = sender.asynchSendAndResponse(OteByteMessageResponse.class, msg, callable, 100);
         result.waitForCompletion();
         long elapsed = System.currentTimeMillis() - time;
         System.out.println(elapsed);
         Assert.assertTrue(result.gotResponse());
         Assert.assertTrue(!result.isTimedOut());
      } finally {
         reg.unregister();
      }
   }
   
   
}

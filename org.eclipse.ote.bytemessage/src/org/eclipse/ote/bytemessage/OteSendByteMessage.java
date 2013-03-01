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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.ote.bytemessage.internal.NotifyOnResponse;
import org.eclipse.ote.bytemessage.internal.OteByteMessageFutureImpl;
import org.eclipse.ote.bytemessage.internal.OteByteMessageResponseFutureImpl;
import org.osgi.service.event.EventAdmin;

public class OteSendByteMessage {

   private final EventAdmin eventAdmin;
   private final Lock lock;
   private final Condition responseReceived;
   
   public OteSendByteMessage(EventAdmin eventAdmin){
      this.eventAdmin = eventAdmin;
      lock = new ReentrantLock();
      responseReceived = lock.newCondition();
   }
   
   /**
    * sends a message and returns immediately
    */
   public void asynchSend(OteByteMessage message) {
      incrementSequenceNumber(message);
      OteByteMessageUtil.postEvent(message, eventAdmin);
   }
   
   /**
    * Registers for a callback of the given message type as specified by the RESPONSE_TOPIC element in the sent message 
    * and the class type passed in, then sends the given message and returns immediately.  The returned value can be used to 
    * wait for the response using waitForCompletion().  The callback expects you to handle both the response and the timeout case. 
    * 
    * @param clazz - Type of OteByteMessage for the response
    * @param message - message to send
    * @param callable - callback executed when the response is recieved or if a timeout occurs or called immediately after the send if 
    *                   no response is expected
    * @param timeout - amount of time in milliseconds to wait for response before calling timeout on the passed in OteByteMessageCallable
    * @return   <T extends OteByteMessage> Future<T> - a future that contains the response message
    */
   public <T extends OteByteMessage, R extends OteByteMessage> OteByteMessageFuture<T, R> asynchSendAndResponse(Class<R> clazz, T message, OteByteMessageCallable<T, R> callable, long timeout){
      int responseId = incrementSequenceNumber(message);
      String responseTopic = message.getHeader().RESPONSE_TOPIC.getValue();
      OteByteMessageFutureImpl<T, R> response = new OteByteMessageFutureImpl<T, R>(clazz, callable, message, responseTopic, responseId, timeout);
      OteByteMessageUtil.postEvent(message, eventAdmin);
      return response;
   }
   
   /**
    * Registers for a callback of the given message type and topic. 
    * 
    * @param clazz - Type of OteByteMessage for the response
    * @param callable - callback executed when the response is recieved
    * @return   a future that you should cancel when done listening so resources can be cleaned up.
    */
   public <R extends OteByteMessage> OteByteMessageResponseFuture<R> asynchResponse(Class<R> clazz, String topic, OteByteMessageResponseCallable<R> callable){
      OteByteMessageResponseFutureImpl<R> response = new OteByteMessageResponseFutureImpl<R>(clazz, callable, topic);
      return response;
   }
   
   /**
    * Sends a message and waits for a response.
    * 
    * @param class - return type
    * @param message - message to send 
    * @param timeout - timeout in milliseconds
    * @return <T extends OteByteMessage> T - NULL if the timeout occurs before a response, otherwise returns the 
    *          message specified by the RESPONSE_TOPIC field in the passed in message header.
    * @throws Exception 
    */
   public <T extends OteByteMessage> T synchSendAndResponse(Class<T> clazz, OteByteMessage message, long timeout) throws Exception {
      lock.lock();
      try{
         int responseId = incrementSequenceNumber(message);
         String responseTopic = message.getHeader().RESPONSE_TOPIC.getValue();
         if(responseTopic.length() == 0){
            throw new Exception(String.format("No responce topic set for [%s]", message.getName()));
         }
         NotifyOnResponse<T> response = new NotifyOnResponse<T>(clazz, responseTopic, responseId, lock, responseReceived);
         try{
            OteByteMessageUtil.postEvent(message, eventAdmin);
            long nanos = TimeUnit.MILLISECONDS.toNanos(timeout);
            while(nanos > 0 && !response.hasResponse()) {
               try {
                  nanos = responseReceived.awaitNanos(nanos);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
         } finally {
            response.dispose();
         }
         return response.getMessage();
      } finally {
         lock.unlock();
      }
   }

   
   
   private int incrementSequenceNumber(OteByteMessage message){
      int responseId = message.getHeader().MESSAGE_SEQUENCE_NUMBER.getValue();
      if(responseId >= Integer.MAX_VALUE){
         responseId = 1;
      } else {
         responseId++;
      }
      message.getHeader().MESSAGE_SEQUENCE_NUMBER.setValue(responseId);
      return responseId;
   }
   
}

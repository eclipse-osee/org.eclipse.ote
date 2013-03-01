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
package org.eclipse.ote.bytemessage.internal;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.eclipse.ote.bytemessage.OteByteMessage;
import org.eclipse.ote.bytemessage.OteByteMessageUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;


public class NotifyOnResponse<T extends OteByteMessage> implements EventHandler {

   private int responseId;
   private Condition responseReceived;
   private Lock lock;
   private Class<T> clazz;
   private T recievedMessage;
   private volatile boolean responded = false;
   private ServiceRegistration<EventHandler> reg;

   public NotifyOnResponse(Class<T> clazz, String responseTopic, int responseId, Lock lock, Condition responseReceived) {
      this.responseId = responseId;
      this.clazz = clazz;
      this.responseReceived = responseReceived;
      this.lock = lock;
      reg = OteByteMessageUtil.subscribe(responseTopic, this);
   }

   @Override
   public void handleEvent(Event event) {
      try {
         recievedMessage = clazz.newInstance();
         OteByteMessageUtil.putBytes(event, recievedMessage);
         if(recievedMessage.getHeader().RESPONSE_ID.getValue() == responseId){
            lock.lock();
            try{
               responded = true;
               responseReceived.signal();
            }finally{
               lock.unlock();
            }
         } else {
            recievedMessage = null;
         }
      } catch (InstantiationException e) {
         recievedMessage = null;
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         recievedMessage = null;
         e.printStackTrace();
      }
   }

   public boolean hasResponse(){
      return responded;
   }
   
   public T getMessage(){
      return recievedMessage;
   }
   
   public void dispose(){
      reg.unregister();
   }
}

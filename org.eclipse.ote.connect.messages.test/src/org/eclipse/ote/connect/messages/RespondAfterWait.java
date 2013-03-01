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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ote.bytemessage.OteByteMessage;
import org.eclipse.ote.bytemessage.OteByteMessageUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;


public class RespondAfterWait implements EventHandler {

   private int wait;
   private OteByteMessage responseMessage;
   private ReentrantLock lock;
   private Condition condition;
   private boolean notCanceled = true;
   
   public RespondAfterWait(OteByteMessage oteByteMessageResponse, int wait) {
      this.wait = wait;
      this.responseMessage = oteByteMessageResponse;
      this.lock = new ReentrantLock();
      this.condition = lock.newCondition();
   }

   @Override
   public void handleEvent(Event event) {
      lock.lock();
      try{
         long nano = TimeUnit.MILLISECONDS.toNanos(wait); 
         while(nano > 0 && notCanceled ){
            try {
               nano = condition.awaitNanos(nano);
            } catch (InterruptedException e) {
               OseeLog.log(getClass(), Level.SEVERE, e);
            }
         }
      } finally {
         lock.unlock();
      }
      if(notCanceled){
         OteByteMessage msg = OteByteMessageUtil.getOteByteMessage(event);
         responseMessage.getHeader().RESPONSE_ID.setValue(msg.getHeader().MESSAGE_SEQUENCE_NUMBER.getValue());
         OteByteMessageUtil.sendEvent(responseMessage);
      }
   }
   
   public void cancel(){
      notCanceled = false;
      lock.lock();
      try{
         condition.signal();
      } finally {
         lock.unlock();
      }
   }

}

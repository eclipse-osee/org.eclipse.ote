/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ote.internal.message.event.send;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.osee.ote.message.event.OteEventMessage;
import org.eclipse.osee.ote.message.event.send.OteEventMessageCallable;


public class TimeoutRunnable<T extends OteEventMessage, R extends OteEventMessage> implements Runnable {

   private final ReentrantLock lock;
   private final Condition condition;
   private final T sentMessage;
   private boolean timedOut = false;
   private final OteEventMessageCallable<T, R> callable;
   private final OteEventMessageFutureImpl<T, R> oteByteMessageFuture;

   public TimeoutRunnable(ReentrantLock lock, Condition condition, T sentMessage, OteEventMessageCallable<T, R> callable, OteEventMessageFutureImpl<T, R> oteByteMessageFuture) {
      this.lock = lock;
      this.condition = condition;
      this.sentMessage = sentMessage;
      this.callable = callable;
      this.oteByteMessageFuture = oteByteMessageFuture;
   }

   @Override
   public void run() {
      lock.lock();
      try{
         timedOut = true;
         try{
            oteByteMessageFuture.cancel();
            callable.timeout(sentMessage);
         } finally {
            condition.signal();
         }
      } finally {
         lock.unlock();
      }
   }
   
   public boolean isTimedOut(){
      return timedOut;
   }

}

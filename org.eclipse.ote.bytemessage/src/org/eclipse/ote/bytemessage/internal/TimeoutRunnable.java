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
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.ote.bytemessage.OteByteMessage;
import org.eclipse.ote.bytemessage.OteByteMessageCallable;


public class TimeoutRunnable<T extends OteByteMessage, R extends OteByteMessage> implements Runnable {

   private ReentrantLock lock;
   private Condition condition;
   private T sentMessage;
   private boolean timedOut = false;
   private OteByteMessageCallable<T, R> callable;
   private OteByteMessageFutureImpl<T, R> oteByteMessageFuture;

   public TimeoutRunnable(ReentrantLock lock, Condition condition, T sentMessage, OteByteMessageCallable<T, R> callable, OteByteMessageFutureImpl<T, R> oteByteMessageFuture) {
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

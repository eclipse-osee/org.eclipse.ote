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

package org.eclipse.ote.statemachine;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class BaseState {

   private int id = -1;
   private ScheduledExecutorService ex;
   private ArrayList<Future<?>> futures;

   public abstract void run(BaseInput input);
   public abstract void entry();
   
   protected final void submitDelayedInput(final BaseInput input, long timeMS){
      if(ex == null){
         ex = Executors.newSingleThreadScheduledExecutor(new ThreadFactory(){
            @Override
            public Thread newThread(Runnable arg0) {
               Thread th = new Thread(arg0);
               th.setName("OteByteMessage Timeout");
               return th;
            }
         });
         futures = new ArrayList<Future<?>>();
      }
      futures.add(ex.schedule(new Runnable() {
         @Override
         public void run() {
            input.addToStateMachineQueue();
         }
      }, timeMS, TimeUnit.MILLISECONDS));
   }
   
   protected final void cancelDelayedInputs(){
      if(ex != null){
         for(Future<?> future:futures){
            future.cancel(true);
         }
         ex.shutdown();
      }
   }
   
   public void exit(){
      cancelDelayedInputs();
   }
   
   int getId() {
      return id;
   }
   
   void setId(int id) {
      this.id = id;
   }
   
}

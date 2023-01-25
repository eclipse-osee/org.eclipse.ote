/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.ote.io;

import java.util.concurrent.ArrayBlockingQueue;

import org.eclipse.ote.io.internal.PoisonPill;

public class DatagramChannelWorker {
   final static PoisonPill POISON_PILL = new PoisonPill();
   private final ArrayBlockingQueue<DatagramChannelData> data;
   private final DatagramChannelRunnable runnable;
   private final String threadName;
   
   public DatagramChannelWorker(String threadName, DatagramChannelRunnable runnable){
      this.runnable = runnable;
      this.threadName = threadName;
      this.data = new ArrayBlockingQueue<DatagramChannelData>(1000);
      
      runnable.setQueue(data);
   }
   
   public void start(){
      Thread th = new Thread(runnable);
      th.setDaemon(true);
      th.setPriority(Thread.MAX_PRIORITY);
      th.setName(threadName);
      th.start();
   }
   
   public void stop() throws InterruptedException{
      submit(POISON_PILL);
   }
   
   public void submit(DatagramChannelData datagramChannelData) throws InterruptedException{
      data.put(datagramChannelData);
   }
   
   public void send(DatagramChannelData datagramChannelData){
      runnable.send(datagramChannelData);
      datagramChannelData.postProcess();
   }
}

/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.ote.message.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;
import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.log.Env;
import org.eclipse.osee.ote.message.IMessageDisposeListener;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.data.MessageData;


/**
 * @author Andrew M. Finkbeiner
 * @author Michael P. Masterson
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class PeriodicPublishTask extends EnvironmentTask implements IMessageDisposeListener {

   private final CopyOnWriteNoIteratorList<Message> periodicMessages = new CopyOnWriteNoIteratorList<Message>(Message.class);
   private final long microSeconds;
   private final int JITTER = 1000;
   private final long largestMiss = 0;
   private final long averageJitter = 0;
   private final long cycleCount = 0;
   private final long jitterMiss = 0;
   private long counter = 0;
   private final ArrayList<MessageData> dataToSend = new ArrayList<MessageData>(100);
   private volatile boolean concurrentStateExceptionLogged;

   public PeriodicPublishTask(double hzRate, int phase) {
      super(hzRate, phase);
      microSeconds = (long) Math.rint(1000.0 / hzRate) * 1000;
   }

   @Override
   public void runOneCycle() throws MessageSystemException {
      try {
         dataToSend.clear();
         counter++;
         Message[] msgs = periodicMessages.get();
         int size = msgs.length;
         for (int i = 0; i < size; i++){
            Message msg = msgs[i];
            if (msg != null) {
               if (!msg.isDestroyed() && !msg.isTurnedOff()) {
                  Stream<MessageData> stream = msg.getMessageData(msg.getMemType()).stream();
                  stream.forEach(msgData -> {
                     if(!dataToSend.contains(msgData)){
                        dataToSend.add(msgData);
                     }
                  });
               }
            }
         }
         size = dataToSend.size();
         for(int i = 0; i < size; i++) {
            MessageData data = dataToSend.get(i);
            try {
               if (data.isMessageCollectionNotEmpty() && data.isScheduled()) {
                  data.send();
               }
            } catch (TestException ex) {
               throw new MessageSystemException(String.format("Failed to send message data [%s]. ", data.getName()),
                     Level.SEVERE, ex);
            }
         }
      } catch (ConcurrentModificationException ex) {
         // Without this catch block the test env is hosed because periodic publish task stops
         if (!concurrentStateExceptionLogged) {
            concurrentStateExceptionLogged = true;
            Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
            OseeLog.log(getClass(), Level.SEVERE, ex);
            for (Thread th : allStackTraces.keySet()) {
               Exception newEx = new Exception("Thread concurrent to ConcurrentModificationException: "+th.getName());
               newEx.setStackTrace(allStackTraces.get(th));
               OseeLog.log(getClass(), Level.SEVERE, newEx);
            }
         }
      }

   }

   public void printTiming() {

      Env.getInstance().message("---------------------------------");
      Env.getInstance().message("rate: " + this.getHzRate() + " : " + microSeconds + "u");
      Env.getInstance().message("avg jitter: " + averageJitter);
      Env.getInstance().message("max jitter: " + largestMiss);
      Env.getInstance().message("num out of bounds jitters @ " + JITTER + " : " + jitterMiss);
      Env.getInstance().message("% of jitter miss: " + ((double) jitterMiss / (double) cycleCount) * 100.0);
      Env.getInstance().message("num of cycles: " + cycleCount);
   }

   public Collection<Message> getMessagesBeingPublished() {
      return  periodicMessages.fillCollection(new LinkedList<Message>());
   }

   public void put(Message msg) {

      if (msg == null) {
         throw new IllegalArgumentException("message cannot be null");
      }
      if (!periodicMessages.contains(msg)) {
         msg.addPreMessageDisposeListener(this);
         periodicMessages.add(msg);
      }
   }

   public void remove(Message msg) {
      msg.removePreMessageDisposeListener(this);
      periodicMessages.remove(msg);
   }

   public void clear() {
      Message[] msgs = periodicMessages.get();
      for (int i = 0; i < msgs.length; i++){
         msgs[i].removePreMessageDisposeListener(this);
      }
      periodicMessages.clear();
   }

   public void print() {

      Env.getInstance().message("rate: " + this.getHzRate());
      Message[] msgs = periodicMessages.get();
      for (Message msg : msgs) {
         Env.getInstance().message(msg.getName());
      }
   }

   @Override
   public void onPostDispose(Message<?, ?, ?> message) {
      // intentionally blank
   }

   @Override
   public void onPreDispose(Message<?, ?, ?> message) {
      periodicMessages.remove(message);
   }

   /**
    * @return the counter
    */
   public long getCounter() {
      return counter;
   }

}

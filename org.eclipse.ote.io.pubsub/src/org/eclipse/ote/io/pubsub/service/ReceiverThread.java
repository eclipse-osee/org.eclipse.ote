/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.io.pubsub.service;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.OseeTestThread;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.message.MessageSystemException;

/**
 * @author Michael P. Masterson
 */
public abstract class ReceiverThread extends OseeTestThread {

   private static final ThreadGroup receiveGroup = new ThreadGroup("Message Receiver Group");
   private volatile boolean run = true;

   public ReceiverThread(String name, boolean isDaemon, TestEnvironment env) {
      super(name, isDaemon, receiveGroup, env);
   }

   public ReceiverThread(String name, TestEnvironment env) {
      this(name, false, env);
   }

   public void handleRunException(Throwable t) throws MessageSystemException{
      if(isRunning()){//suppress the log when we kill the reciever
         throw new MessageSystemException(getName() + " has stopped receiving messages and has been terminated", Level.SEVERE, t);
      }
   }

   public void kill() {
      if (isRunning()) {
         OseeLog.log(ReceiverThread.class, Level.FINE, new Exception("killing the thread"));
         setRunning(false);
         interrupt();
      }
   }

   /**
    * @param run the run to set
    */
   public void setRunning(boolean run) {
      this.run = run;
   }

   /**
    * @return the run
    */
   public boolean isRunning() {
      return run;
   }
}

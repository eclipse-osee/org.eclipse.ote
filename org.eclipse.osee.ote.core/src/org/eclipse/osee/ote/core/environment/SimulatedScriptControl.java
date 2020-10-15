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
package org.eclipse.osee.ote.core.environment;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Michael P. Masterson
 */
public class SimulatedScriptControl extends ScriptControl {

   private ReentrantLock scriptLock = null;

   public SimulatedScriptControl() {
      scriptLock = new ReentrantLock();
   }

   @Override
   public void lock() {
      scriptLock.lock();
   }

   @Override
   public void unlock() {
      if (scriptLock.isHeldByCurrentThread()) {
         scriptLock.unlock();
      } else {
         try {
            if (Thread.currentThread().isInterrupted()) {
               Thread.interrupted();
            }
            scriptLock.tryLock(30000, TimeUnit.MILLISECONDS);
            if (scriptLock.isHeldByCurrentThread()) {
               scriptLock.unlock();
            }
         } catch (InterruptedException ex) {
            ex.printStackTrace();
         }
      }
   }

   @Override
   public boolean isLocked() {
      return scriptLock.isLocked();
   }

   @Override
   public boolean hasLock() {
      return true;
   }

   @Override
   public boolean isHeldByCurrentThread() {
      return scriptLock.isHeldByCurrentThread();
   }
}

/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.message.mock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ken J. Aguilar
 */
class SequenceHandle implements ISequenceHandle {

   private final ReentrantLock lock = new ReentrantLock();
   private final Condition endSequenceCondition = lock.newCondition();
   private boolean endSequence = false;

   @Override
   public boolean waitForEndSequence(long timeout, TimeUnit timeUnit) throws InterruptedException {
      lock.lock();
      long nanos = timeUnit.toNanos(timeout);
      try {
         while (!endSequence) {
            if (nanos > 0) {
               nanos = endSequenceCondition.awaitNanos(nanos);
            } else {
               return false;
            }
         }
         return true;
      } finally {
         lock.unlock();
      }
   }

   void signalEndSequence() {
      lock.lock();
      try {
         endSequence = true;
         endSequenceCondition.signalAll();
      } finally {
         lock.unlock();
      }
   }
}

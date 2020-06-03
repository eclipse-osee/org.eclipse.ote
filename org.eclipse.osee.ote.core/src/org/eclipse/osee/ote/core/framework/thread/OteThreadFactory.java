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

package org.eclipse.osee.ote.core.framework.thread;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadFactory;

/**
 * @author Roberto E. Escobar
 */
public class OteThreadFactory implements ThreadFactory {

   private final List<WeakReference<OteThread>> threads;
   private final String threadName;

   protected OteThreadFactory(String threadName) {
      this.threadName = threadName;
      this.threads = new CopyOnWriteArrayList<>();
   }

   @Override
   public Thread newThread(Runnable runnable) {
      OteThread thread = new OteThread(runnable, threadName + ":" + threads.size());
      this.threads.add(new WeakReference<OteThread>(thread));
      return thread;
   }

   public List<OteThread> getThreads() {
      List<OteThread> toReturn = new ArrayList<>();
      for (WeakReference<OteThread> weak : threads) {
         OteThread thread = weak.get();
         if (thread != null) {
            toReturn.add(thread);
         }
      }
      return toReturn;
   }
}

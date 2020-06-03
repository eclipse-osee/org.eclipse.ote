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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

/**
 * @author Roberto E. Escobar
 */
public class OteThreadManager {

   private static OteThreadManager instance = null;

   private final Map<String, OteThreadFactory> factories;

   private OteThreadManager() {
      this.factories = new HashMap<>();
   }

   public static OteThreadManager getInstance() {
      if (instance == null) {
         instance = new OteThreadManager();
      }
      return instance;
   }

   public ThreadFactory createNewFactory(String threadName) {
      OteThreadFactory factory = new OteThreadFactory(threadName);
      factories.put(threadName, factory);
      return factory;
   }

   public List<OteThread> getThreadsFromFactory(String key) {
      OteThreadFactory factory = factories.get(key);
      return factory.getThreads();
   }

   public Set<String> getFactories() {
      return factories.keySet();
   }
}
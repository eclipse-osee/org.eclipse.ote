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

import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;

/**
 * This is a helper class that stores the periodic publish tasks in a map based on the rate and phase of a message.
 * Currently we're ignoring the phase, by setting it to zero, because we determined it was not important, this may
 * change in the future.
 *
 * @author Andrew M. Finkbeiner
 * @author Michael P. Masterson
 */
public class PeriodicPublishMap {
   private final CompositeKeyHashMap<Double, Integer, PeriodicPublishTask> ratePhaseMap =
         new CompositeKeyHashMap<Double, Integer, PeriodicPublishTask>(32, false);

   public void clear() {
      for (PeriodicPublishTask task : ratePhaseMap.values()) {
         task.clear();
      }
      ratePhaseMap.clear();
   }

   public PeriodicPublishTask get(double rate, int phase) {
      phase = 0;// ignoring phase, delete to stop ignoring
      return ratePhaseMap.get(rate, phase);
   }

   public PeriodicPublishTask put(double rate, int phase, PeriodicPublishTask task) {
      phase = 0;// ignoring phase, delete to stop ignoring
      ratePhaseMap.put(rate, phase, task);
      return task;
   }

   public boolean containsKey(double rate, int phase) {
      phase = 0;// ignoring phase, delete to stop ignoring
      return ratePhaseMap.containsKey(rate, phase);
   }

   public PeriodicPublishTask[] getTasks() {
      return ratePhaseMap.values().toArray(new PeriodicPublishTask[ratePhaseMap.size()]);
   }

   /**
    * @return the ratePhaseMap
    */
   public CompositeKeyHashMap<Double, Integer, PeriodicPublishTask> getRatePhaseMap() {
      return ratePhaseMap;
   }

}

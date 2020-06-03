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

package org.eclipse.osee.ote.core.environment.interfaces;

import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.framework.IRunManager;

public interface ITimerControl {
   void addTask(EnvironmentTask task, TestEnvironment environment);

   void removeTask(EnvironmentTask task);

   void cancelTimers();

   long getEnvTime();

   ICancelTimer setTimerFor(ITimeout objToNotify, int milliseconds);

   void envWait(ITimeout obj, int milliseconds) throws InterruptedException;

   void envWait(int milliseconds) throws InterruptedException;

   int getCycleCount();

   void incrementCycleCount();

   void setCycleCount(int cycle);

   void dispose();

   public void cancelAllTasks();

   public void step();

   long getTimeOfDay();

   public void setRunManager(IRunManager runManager);

   public IRunManager getRunManager();

   public boolean isRealtime();
}

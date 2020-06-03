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

import java.util.Set;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public interface ITestEnvironmentAccessor {
   void abortTestScript();

   boolean addTask(EnvironmentTask task);

   void associateObject(Class<?> c, Object obj);

   Object getAssociatedObject(Class<?> c);

   Set<Class<?>> getAssociatedObjects();

   TestScript getTestScript();

   long getEnvTime();

   IExecutionUnitManagement getExecutionUnitManagement();

   ITestLogger getLogger();

   IScriptControl getScriptCtrl();

   ITestStation getTestStation();

   ITimerControl getTimerCtrl();

   void onScriptComplete() throws InterruptedException;

   void onScriptSetup();

   ICancelTimer setTimerFor(ITimeout listener, int time);
}

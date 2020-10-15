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

import java.lang.ref.WeakReference;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;

/**
 * A simple implementation of an environment task used to run the primary cycle of an execution unit. 
 * 
 * Generally this can be used in those environments where the OTE controls the UUT cycle. 
 * @author Michael P. Masterson
 */
public class SimulatedExecutionUnitTask extends EnvironmentTask {

   private final WeakReference<ITestEnvironmentAccessor> env;

   public SimulatedExecutionUnitTask(double hzRate, ITestEnvironmentAccessor env) {
      super(hzRate);
      this.env = new WeakReference<ITestEnvironmentAccessor>(env);
   }

   @Override
   public void runOneCycle() throws InterruptedException {
      env.get().getExecutionUnitManagement().runPrimaryOneCycle();
   }
}
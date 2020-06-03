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

package org.eclipse.osee.ote.core.framework.command;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.ote.core.environment.TestEnvironment;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestCommandComplete implements Callable<ITestCommandResult> {

   private ICommandHandle handle;
   private final TestEnvironment env;
   private Future<ITestCommandResult> future;
   private ITestServerCommand cmd;

   public TestCommandComplete(TestEnvironment env, ITestServerCommand cmd, Future<ITestCommandResult> future) {
      this.future = future;
      this.cmd = cmd;
      this.env = env;
   }

   public TestCommandComplete(TestEnvironment env, ICommandHandle handle) {
      this.env = env;
      this.handle = handle;
   }

   @Override
   public ITestCommandResult call() throws Exception {
      ITestCommandResult result;
      try {
         result = future.get(30, TimeUnit.SECONDS);
         ICommandHandle handle = cmd.createCommandHandle(future, env);
         env.testEnvironmentCommandComplete(handle);
      } catch (Throwable th) {
         result = new TestCommandResult(TestCommandStatus.FAIL, new Exception("Failed to retrieve command result", th));
      }
      return result;
   }

}

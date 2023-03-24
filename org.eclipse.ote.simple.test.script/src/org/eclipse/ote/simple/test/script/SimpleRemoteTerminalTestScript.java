/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.ote.simple.test.script;

import org.eclipse.osee.ote.core.annotations.Order;
import org.eclipse.osee.ote.core.environment.jini.ITestEnvironmentCommandCallback;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.remote.terminal.OteRemoteTerminal;
import org.eclipse.osee.ote.remote.terminal.OteRemoteTerminalResponse;
import org.eclipse.ote.simple.test.environment.SimpleOteApi;
import org.junit.Test;

/**
 * @author Nydia Delgado
 */
public class SimpleRemoteTerminalTestScript extends SimpleMessageSystemTestScript {

   public SimpleRemoteTerminalTestScript(MessageSystemTestEnvironment testEnvironment,
         ITestEnvironmentCommandCallback callback) {
      super(testEnvironment, callback);
   }

   @Test
   @Order(1)
   public void remoteTerminalTestCase(SimpleOteApi simpleApi) throws Exception {
      OteRemoteTerminal rt = simpleApi.remoteTerminal();
      rt.open();

      if (rt.isConnected()) {
         OteRemoteTerminalResponse resp = rt.command("cat /proc/sys/kernel/hostname");
         resp.verifyStandardOut(this, rt.getHostName() + "\n");
         resp.verifyExitCode(this, 0);

         rt.close();
      }
   }

   @Test
   @Order(2)
   public void remoteTerminalStdErrTestCase(SimpleOteApi simpleApi) throws Exception {
      simpleApi.promptPause("Starting Remote Terminal StdErr Test Case");

      OteRemoteTerminal rt = simpleApi.remoteTerminal();
      rt.open();

      OteRemoteTerminalResponse resp = rt.command("some bad command");
      resp.verifyStandardErrorContains(this, "ksh: some: not found");
      resp.verifyExitCode(this, 127);

      rt.close();
   }
}

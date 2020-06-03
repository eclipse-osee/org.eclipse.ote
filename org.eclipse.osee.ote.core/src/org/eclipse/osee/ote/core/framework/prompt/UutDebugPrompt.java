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

package org.eclipse.osee.ote.core.framework.prompt;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.TestScript;

/**
 * @author Ken J. Aguilar
 */
public class UutDebugPrompt extends ScriptPausePromptImpl {

   public UutDebugPrompt(IServiceConnector connector, TestScript script, String id, String message) throws UnknownHostException {
      super(connector, script, id, message);
   }

   @Override
   public void resume() throws RemoteException {
      super.resume();
   }

   @Override
   protected String waitForResponse(TestScript script, boolean executionUnitPause) throws InterruptedException, Exception {
      synchronized (script) {
         script.getTestEnvironment().getScriptCtrl().setScriptPause(true);
         script.getTestEnvironment().getScriptCtrl().setExecutionUnitPause(executionUnitPause);
         script.getTestEnvironment().getScriptCtrl().unlock();
         script.wait();
         if (exception != null) {
            throw exception;
         }
         return response;
      }
   }

}

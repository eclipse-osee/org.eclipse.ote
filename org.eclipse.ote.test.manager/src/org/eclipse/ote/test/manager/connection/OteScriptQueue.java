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

package org.eclipse.ote.test.manager.connection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystem;
import org.eclipse.osee.ote.ui.test.manager.connection.ScriptQueue;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.models.ScriptModel;
import org.eclipse.osee.ote.ui.test.manager.pages.contributions.TestManagerStorageKeys;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;
import org.eclipse.ote.test.manager.internal.OteTestManagerPlugin;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OteScriptQueue extends ScriptQueue {

   public OteScriptQueue(List<ScriptTask> scripts, TestManagerEditor testManager) {
      super(scripts, testManager);
   }

   private void runOteFuncTests(List<ScriptTask> scripts) throws IllegalArgumentException {
      // Update the local class server for any changes
      getTestManagerEditor().getScriptClassServer().addAnyNewProjects();

      IPropertyStore propertyStore = getTestManagerEditor().getPropertyStore();
      String outputDir = propertyStore.get(TestManagerStorageKeys.SCRIPT_OUTPUT_DIRECTORY_KEY);
      boolean isBatchMode = propertyStore.getBoolean(TestManagerStorageKeys.BATCH_MODE_ENABLED_KEY);

      try {
         ITestEnvironmentMessageSystem environment =
            (ITestEnvironmentMessageSystem) getTestManagerEditor().getConnectedEnvironment();
         environment.setBatchMode(isBatchMode);
         List<ScriptModel.TestFileData> runthese = new ArrayList<>();
         for (ScriptTask scriptTask : scripts) {
            OteTestManagerPlugin.getInstance().getOteConsoleService().write(
               "Submitting " + scriptTask.getName() + "...");
            ScriptModel script = scriptTask.getScriptModel();
            ScriptModel.TestFileData javafileData = script.updateScriptModelInfo(outputDir);
            script.getOutputModel().setPassedTestPoints(0);
            script.getOutputModel().setFailedTestPoints(0);
            script.getOutputModel().setInteractiveTestPoints(0);
            script.getOutputModel().setAborted(false);
            OseeLog.log(OteTestManagerPlugin.class, Level.INFO, "sunData.sunOutFile *" + javafileData.outFile + "*");
            GUID guid = null;
            runthese.add(javafileData);
            getScriptManager().notifyScriptQueued(guid, scriptTask);
         }
         getScriptManager().runScripts(runthese);
      } catch (RemoteException e) {
         OteTestManagerPlugin.getInstance().getOteConsoleService().writeError(Lib.exceptionToString(e));
      } catch (URISyntaxException e) {
         OteTestManagerPlugin.getInstance().getOteConsoleService().writeError(Lib.exceptionToString(e));
      } catch (InterruptedException e) {
         OteTestManagerPlugin.getInstance().getOteConsoleService().writeError(Lib.exceptionToString(e));
      } catch (ExecutionException e) {
         OteTestManagerPlugin.getInstance().getOteConsoleService().writeError(Lib.exceptionToString(e));
      } catch (IOException e) {
         OteTestManagerPlugin.getInstance().getOteConsoleService().writeError(Lib.exceptionToString(e));
      }
   }

   @Override
   public void run() {
      try {
         runOteFuncTests(getScriptsToExecute());
      } catch (IllegalArgumentException ex) {
         OteTestManagerPlugin.log(Level.SEVERE, "exception running tests", ex);
         OteTestManagerPlugin.getInstance().getOteConsoleService().writeError(
            "Exception during tests execution. See Error Log");
         getTestManagerEditor().executionCompleted();
      }
   }

   @Override
   protected OteScriptManager getScriptManager() {
      return (OteScriptManager) super.getScriptManager();
   };
}

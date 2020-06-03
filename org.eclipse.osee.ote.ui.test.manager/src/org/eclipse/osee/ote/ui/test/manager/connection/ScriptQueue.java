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

package org.eclipse.osee.ote.ui.test.manager.connection;

import java.util.List;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;

public abstract class ScriptQueue implements Runnable {

   private final List<ScriptTask> scripts;
   private final TestManagerEditor testManager;

   public ScriptQueue(List<ScriptTask> scripts, TestManagerEditor testManager) {
      super();
      this.scripts = scripts;
      this.testManager = testManager;
   }

   @Override
   public abstract void run();

   protected List<ScriptTask> getScriptsToExecute() {
      return scripts;
   }

   protected TestManagerEditor getTestManagerEditor() {
      return testManager;
   }

   protected ScriptManager getScriptManager() {
      return testManager.getPageManager().getScriptPage().getScriptManager();
   }
}

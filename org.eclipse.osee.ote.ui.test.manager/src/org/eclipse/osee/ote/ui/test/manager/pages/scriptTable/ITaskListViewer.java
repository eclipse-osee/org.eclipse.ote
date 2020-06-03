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

package org.eclipse.osee.ote.ui.test.manager.pages.scriptTable;

public interface ITaskListViewer {

   /**
    * Update the view to reflect the fact that a task was added to the task list
    */
   public void addTask(ScriptTask task);

   /**
    * Update the view to reflect the fact that multiple tasks were added to the task list
    */
   public void addTasks(ScriptTask[] tasks);

   /**
    * Update the view to reflect the fact that a task was removed from the task list
    */
   public void removeTask(ScriptTask task);

   /**
    * Update the view to reflect the fact that one of the tasks was modified
    */
   public void updateTask(ScriptTask task);
}

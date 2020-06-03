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

package org.eclipse.osee.ote.core.environment.console.cmd;

import org.eclipse.osee.ote.core.environment.console.ConsoleCommand;
import org.eclipse.osee.ote.core.environment.console.ConsoleShell;

/**
 * @author Ken J. Aguilar
 */
public class LastCmd extends ConsoleCommand {

   private static final String NAME = "lc";
   private static final String DESCRIPTION = "Executes the last command executed";

   public LastCmd() {
      super(NAME, DESCRIPTION);
   }

   @Override
   protected void doCmd(ConsoleShell shell, String[] switches, String[] args) {
      shell.runLast();
      shell.setSaveLastCmdFlag(false);
   }

}

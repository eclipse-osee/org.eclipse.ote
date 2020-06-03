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

package org.eclipse.ote.message.manager;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.osee.ote.core.environment.console.ConsoleCommand;
import org.eclipse.osee.ote.core.environment.console.ICommandManager;
import org.eclipse.ote.services.core.ServiceUtility;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ken J. Aguilar
 * @author Michael P. Masterson
 */
public class ConsoleCommandTracker extends ServiceTracker<ICommandManager, Object> {

   private final ArrayList<ConsoleCommand> commands;

   public ConsoleCommandTracker(ConsoleCommand... commands) {
      super(ServiceUtility.getContext(), ICommandManager.class.getName(), null);
      this.commands = new ArrayList<ConsoleCommand>(Arrays.asList(commands));
   }

   @Override
   public Object addingService(ServiceReference<ICommandManager> reference) {
      ICommandManager manager = (ICommandManager) super.addingService(reference);
      for (ConsoleCommand cmd : commands) {
         manager.registerCommand(cmd);
      }
      return manager;
   }

   private void unregisterCommands(ICommandManager manager) {
      for (ConsoleCommand cmd : commands) {
         manager.unregisterCommand(cmd);
      }
      commands.clear();
   }

   @Override
   public synchronized void close() {
      ICommandManager service = (ICommandManager) getService();
      if (service != null) {
         unregisterCommands(service);
      }
      super.close();
   }

}

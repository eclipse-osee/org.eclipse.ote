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

package org.eclipse.osee.ote.core.environment.console;

import java.util.Collection;
import java.util.Hashtable;

public class ConsoleCommandManager implements ICommandManager {

   private final Hashtable<String, ConsoleCommand> cmdMap = new Hashtable<>(64);

   private boolean isShutdown = false;

   @Override
   public void registerCommand(ConsoleCommand cmd) {
      if (!isShutdown) {
         cmdMap.put(cmd.getName(), cmd);
      } else {
         throw new IllegalStateException("Can't register command: This manager has been shutdown");
      }
   }

   @Override
   public ConsoleCommand unregisterCommand(ConsoleCommand cmd) {
      if (!isShutdown) {
         return cmdMap.remove(cmd.getName());
      } else {
         throw new IllegalStateException("Can't register command: This manager has been shutdown");
      }
   }

   @Override
   public ConsoleCommand getCommand(String name) {
      return cmdMap.get(name);
   }

   @Override
   public Collection<ConsoleCommand> getCommands() {
      return cmdMap.values();
   }

   public void shutdown() {
      isShutdown = true;
      for (ConsoleCommand cmd : cmdMap.values()) {
         cmd.dispose();
      }
      cmdMap.clear();
   }
}

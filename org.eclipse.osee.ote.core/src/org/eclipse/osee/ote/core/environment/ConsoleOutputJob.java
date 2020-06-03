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

package org.eclipse.osee.ote.core.environment;

import org.eclipse.osee.ote.core.IUserSession;

public class ConsoleOutputJob implements Runnable {

   private final IUserSession callback;
   private final String message;

   public ConsoleOutputJob(IUserSession callback, String message) {
      this.callback = callback;
      this.message = message;
   }

   @Override
   public void run() {
      try {
         callback.initiateInformationalPrompt(message);
      } catch (Throwable e) {
         System.out.println(message);
      }
   }

}

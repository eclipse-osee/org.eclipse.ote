/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.environment.command;

import java.io.Serializable;
import java.util.UUID;

import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.IServiceCommand;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public abstract class TestEnvironmentCommand implements Serializable, IServiceCommand {
   private static final long serialVersionUID = -921447917279635626L;
   private final UUID key;
   private final CommandDescription commandDescription;

   public TestEnvironmentCommand(UUID key, CommandDescription commandDescription) {
      this.key = key;
      this.commandDescription = commandDescription;
   }

   public UUID getUserKey() {
      return key;
   }

   public void executeBase(TestEnvironment environment) {
      execute(environment);
   }

   @Override
   public abstract void execute(TestEnvironment environment) throws TestException;

   public CommandDescription getDescription() {
      return commandDescription;
   }

}
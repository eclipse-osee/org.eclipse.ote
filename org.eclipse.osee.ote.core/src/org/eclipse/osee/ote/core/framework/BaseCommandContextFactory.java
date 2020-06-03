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

package org.eclipse.osee.ote.core.framework;

import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.framework.command.ITestContext;
import org.eclipse.osee.ote.core.framework.command.ITestServerCommand;

public class BaseCommandContextFactory implements ICommandContextFactory {
   @Override
   public ITestContext getContext(final TestEnvironment testEnvironment, final ITestServerCommand cmd) {
      return testEnvironment;
   }
}
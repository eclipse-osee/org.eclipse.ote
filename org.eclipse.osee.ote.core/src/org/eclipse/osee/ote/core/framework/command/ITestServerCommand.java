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

package org.eclipse.osee.ote.core.framework.command;

import java.rmi.server.ExportException;
import java.util.UUID;
import java.util.concurrent.Future;

import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.status.OTEStatusBoard;

public interface ITestServerCommand {

   UUID getUserSessionKey();

   ICommandHandle createCommandHandle(Future<ITestCommandResult> result, ITestContext context) throws ExportException;

   ITestCommandResult execute(TestEnvironment context, OTEStatusBoard statusBoard) throws Exception;

   String getGUID();
}

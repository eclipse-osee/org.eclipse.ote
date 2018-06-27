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

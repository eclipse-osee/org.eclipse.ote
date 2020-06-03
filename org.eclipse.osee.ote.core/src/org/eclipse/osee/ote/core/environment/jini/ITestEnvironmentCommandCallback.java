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

package org.eclipse.osee.ote.core.environment.jini;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.eclipse.osee.ote.core.TestPrompt;
import org.eclipse.osee.ote.core.environment.status.ExceptionEvent;

/**
 * @author Andrew M. Finkbeiner
 */
public interface ITestEnvironmentCommandCallback extends Remote {
   void initiatePrompt(TestPrompt prompt) throws RemoteException;

   void exceptionReceived(ExceptionEvent event) throws RemoteException;

   long getFileDate(String workspacePath) throws RemoteException;

   byte[] getFile(String workspacePath) throws RemoteException;

   Object[] getValues(String key) throws RemoteException;

   boolean isAlive() throws RemoteException;

   String getFileVersion(String workspacePath) throws RemoteException;

   String getAddress() throws RemoteException;
}

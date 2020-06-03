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

package org.eclipse.osee.ote.core.model;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.TestEnvironment;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IModel extends Remote {
   void turnModelOn() throws RemoteException;

   void turnModelOff() throws RemoteException;

   void init(TestEnvironment testEnvironment, ModelKey key) throws RemoteException;

   ModelState getState() throws RemoteException;

   <CLASSTYPE extends IModel> ModelKey getKey() throws RemoteException;

   void dispose() throws RemoteException;

   EnvironmentTask getEnvironmentTask() throws RemoteException;
}

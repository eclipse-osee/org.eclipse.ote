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

package org.eclipse.osee.ote.core.environment.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.eclipse.osee.ote.core.model.IModelManagerRemote;
import org.eclipse.osee.ote.message.IInstrumentationRegistrationListener;

/**
 * @author Andrew M. Finkbeiner
 */
public interface ITestEnvironment extends Remote {

   Remote getControlInterface(String controlInterfaceID) throws RemoteException;

   IRemoteCommandConsole getCommandConsole() throws RemoteException;
   public void closeCommandConsole(IRemoteCommandConsole console) throws RemoteException;

   public IModelManagerRemote getModelManager() throws RemoteException;

   byte[] getScriptOutfile(String outfilePath) throws RemoteException;

   int getUniqueId() throws RemoteException;

   public void setBatchMode(boolean isBatched) throws RemoteException;

//   public void sendMessage(Message message) throws RemoteException;
   
   void addInstrumentationRegistrationListener(IInstrumentationRegistrationListener listener) throws RemoteException;

   void removeInstrumentationRegistrationListener(IInstrumentationRegistrationListener listener) throws RemoteException;


}
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

package org.eclipse.osee.ote.core;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.eclipse.osee.ote.core.framework.prompt.IPassFailPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IResumeResponse;
import org.eclipse.osee.ote.core.framework.prompt.IUserInputPromptResponse;

/**
 * @author Ken J. Aguilar
 */
public interface IRemoteUserSession extends Remote, IUserSession {
   @Override
   public String getAddress() throws RemoteException;

   @Override
   public OSEEPerson1_4 getUser() throws RemoteException;

   @Override
   public byte[] getFile(String workspacePath) throws RemoteException;

   @Override
   public long getFileDate(String workspacePath) throws RemoteException;

   @Override
   public void initiatePassFailPrompt(IPassFailPromptResponse prompt) throws RemoteException;

   @Override
   public void initiateUserInputPrompt(IUserInputPromptResponse prompt) throws RemoteException;

   @Override
   public void initiateResumePrompt(IResumeResponse prompt) throws RemoteException;

   @Override
   public void initiateInformationalPrompt(String message) throws RemoteException;

   @Override
   public boolean isAlive() throws RemoteException;

//   @Override
//   public void sendMessageToClient(Message message) throws RemoteException;

}

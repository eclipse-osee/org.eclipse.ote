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

package org.eclipse.osee.ote.message.interfaces;

import java.net.InetSocketAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

import org.eclipse.osee.ote.message.enums.DataType;

public interface IMsgToolServiceClient extends Remote {
   InetSocketAddress getAddressByType(String messageName, DataType memType) throws RemoteException;

   void changeRate(String msgName, double rate) throws RemoteException;

   void changeIsScheduled(String msgName, boolean isScheduled) throws RemoteException;

   UUID getTestSessionKey() throws RemoteException;
}

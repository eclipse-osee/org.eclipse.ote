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

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

package org.eclipse.osee.ote.message.instrumentation;

import java.net.InetSocketAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IOInstrumentation extends Remote {

   void register(InetSocketAddress address) throws RemoteException;

   void command(byte[] cmd) throws RemoteException;

   void unregister(InetSocketAddress address) throws RemoteException;
}

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
package org.eclipse.osee.ote.core.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IModelListener extends Remote {

   void onModelPreCreate(ModelKey key) throws RemoteException;

   void onModelPostCreate(ModelKey key) throws RemoteException;

   void onModelDispose(ModelKey key) throws RemoteException;

   void onModelStateChange(ModelKey key, ModelState state) throws RemoteException;

   int getHashCode() throws RemoteException;
}

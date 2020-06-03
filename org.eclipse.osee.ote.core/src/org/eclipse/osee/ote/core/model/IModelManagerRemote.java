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
import java.util.List;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IModelManagerRemote extends Remote {
   List<ModelKey> getRegisteredModels() throws RemoteException;

   Remote getRemoteModel(ModelKey<?> key) throws RemoteException;

   ModelState getModelState(ModelKey<?> key) throws RemoteException;

   void releaseReference(ModelKey<?> key) throws RemoteException;

   void addModelActivityListener(IModelListener listener) throws RemoteException;

   void removeModelActivityListener(IModelListener listener) throws RemoteException;

   void addModelActivityListener(IModelListener listener, ModelKey<?> key) throws RemoteException;

   void removeModelActivityListener(IModelListener listener, ModelKey<?> key) throws RemoteException;

   void changeModelState(ModelKey<?> key, ModelState state) throws RemoteException;

   void releaseAllReferences(ModelKey<?> key) throws RemoteException;
}

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

package org.eclipse.ote.ui.mux.view;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.eclipse.osee.ote.message.IInstrumentationRegistrationListener;
import org.eclipse.osee.ote.message.instrumentation.IOInstrumentation;

/**
 * @author Ken J. Aguilar
 */
public interface IRegistrationListener extends Remote, IInstrumentationRegistrationListener {

   @Override
   void onRegistered(String name, IOInstrumentation instrumentation) throws RemoteException;

   @Override
   void onDeregistered(String name) throws RemoteException;
}

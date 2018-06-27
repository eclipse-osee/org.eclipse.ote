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

import java.rmi.RemoteException;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.message.IInstrumentationRegistrationListener;

/**
 * @author Andrew M. Finkbeiner
 */
public interface ITestEnvironmentMessageSystem extends ITestEnvironment {

   @Override
   void addInstrumentationRegistrationListener(IInstrumentationRegistrationListener listener) throws RemoteException;

   @Override
   void removeInstrumentationRegistrationListener(IInstrumentationRegistrationListener listener) throws RemoteException;

}

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

package org.eclipse.osee.ote.connection.service.test;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Ken J. Aguilar
 */
public class TestJiniService implements Remote {

   public void doSomething() throws RemoteException {
      System.out.println("doing something...");
   }
}

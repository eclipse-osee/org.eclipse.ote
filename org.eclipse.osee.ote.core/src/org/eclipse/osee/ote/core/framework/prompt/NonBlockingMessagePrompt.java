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

package org.eclipse.osee.ote.core.framework.prompt;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.IUserSession;

/**
 * @author Ken J. Aguilar
 */
public class NonBlockingMessagePrompt extends AbstractRemotePrompt implements IResumeResponse {

   public NonBlockingMessagePrompt(IServiceConnector connector, String id, String message) throws UnknownHostException {
      super(connector, id, message);
   }

   public void open(IUserSession session) throws Exception {

      session.initiateResumePrompt(this);

   }

   @Override
   public void resume() throws RemoteException {

   }

}

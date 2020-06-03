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
import org.eclipse.osee.ote.core.TestScript;

/**
 * @author Ken J. Aguilar
 */
public class PassFailPromptImpl extends AbstractInteractivePrompt<PassFailPromptResult> implements IPassFailPromptResponse {

   public PassFailPromptImpl(IServiceConnector connector, TestScript script, String id, String message) throws UnknownHostException {
      super(connector, script, id, message);
   }

   @Override
   public void doPrompt() throws Exception {
      getScript().getUserSession().initiatePassFailPrompt(createRemoteReference(IPassFailPromptResponse.class));
   }

   @Override
   public void respond(boolean pass, String text) throws RemoteException {
      endPrompt(new PassFailPromptResult(pass, text), null);
   }
}

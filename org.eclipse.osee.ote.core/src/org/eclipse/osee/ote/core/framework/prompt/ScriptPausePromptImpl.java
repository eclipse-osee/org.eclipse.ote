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
public class ScriptPausePromptImpl extends AbstractInteractivePrompt<String> implements IResumeResponse {

   public ScriptPausePromptImpl(IServiceConnector connector, TestScript script, String id, String message) throws UnknownHostException {
      super(connector, script, id, message);
   }

   @Override
   public void doPrompt() throws Exception {
      getScript().getUserSession().initiateResumePrompt(createRemoteReference(IResumeResponse.class));
   }

   @Override
   public void resume() throws RemoteException {
      endPrompt("CONTINUE", null);
   }
}

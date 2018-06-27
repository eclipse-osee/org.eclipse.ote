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
package org.eclipse.osee.ote.core.framework.prompt;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import org.eclipse.osee.connection.service.IServiceConnector;

/**
 * @author Ken J. Aguilar
 */
public class AbstractRemotePrompt implements IPromptHandle {
   private final String id;
   private final String message;
   private final IServiceConnector connector;

   public AbstractRemotePrompt(IServiceConnector connector, String id, String message) throws UnknownHostException {
      this.connector = connector;
      this.id = id;
      this.message = message;
   }

   @Override
   public String getPromptId() throws RemoteException {
      return id;
   }

   @Override
   public String getPromptMessage() throws RemoteException {
      return message;
   }

   /**
    * returns a reference to the specified remote interface whose remote methods will be bound to this prompt object
    * 
    */
   protected final <U extends IPromptHandle> U createRemoteReference(Class<U> remoteInterface) throws ExportException {
      return remoteInterface.cast(connector.export(this));
   }

   public void close() throws Exception {
      connector.unexport(this);
   }
}

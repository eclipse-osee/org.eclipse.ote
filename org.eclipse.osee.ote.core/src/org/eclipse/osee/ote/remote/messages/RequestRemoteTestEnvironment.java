/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ote.remote.messages;

import java.io.Serializable;
import java.util.UUID;

import org.eclipse.osee.ote.core.IRemoteUserSession;
import org.eclipse.osee.ote.core.environment.TestEnvironmentConfig;

public class RequestRemoteTestEnvironment implements Serializable {
   
   private static final long serialVersionUID = -6720107128761044291L;
  
   private IRemoteUserSession session;
   private UUID id;
   private TestEnvironmentConfig config;
   
   public RequestRemoteTestEnvironment(IRemoteUserSession session, UUID id, TestEnvironmentConfig config){
      this.session = session;
      this.id = id;
      this.config = config;
   }

   public IRemoteUserSession getSession() {
      return session;
   }

   public UUID getId() {
      return id;
   }

   public TestEnvironmentConfig getConfig() {
      return config;
   }

}

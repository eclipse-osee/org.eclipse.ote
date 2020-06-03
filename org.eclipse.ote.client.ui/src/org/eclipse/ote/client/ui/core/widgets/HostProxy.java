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

package org.eclipse.ote.client.ui.core.widgets;

import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.util.UUID;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.OTETestEnvironmentClient;
import org.eclipse.osee.ote.core.ConnectionRequestResult;
import org.eclipse.osee.ote.core.IRemoteUserSession;
import org.eclipse.osee.ote.core.environment.TestEnvironmentConfig;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.master.rest.model.OTEServer;
import org.eclipse.osee.ote.properties.OtePropertiesCore;

/**
 * @author Andrew M. Finkbeiner
 */
public class HostProxy implements IHostTestEnvironment {

      private final OTETestEnvironmentClient client;
      private final OTEServer server;
            
      private static final long getPropertiesTimeout = OtePropertiesCore.pingTimeout.getLongValue(1000);
      
      public HostProxy(OteUdpEndpoint service, InetSocketAddress address, OTEServer server) {
         this.server = server;
         this.client = new OTETestEnvironmentClient(service, address);
      }

      @Override
      public EnhancedProperties getProperties() {
         return client.getProperties(getPropertiesTimeout);
      }
      
      public EnhancedProperties getProperties(long timeout) {
         return client.getProperties(timeout);
      }

      @Override
      public ConnectionRequestResult requestEnvironment(IRemoteUserSession session, UUID id, TestEnvironmentConfig config) throws RemoteException {
         Thread.currentThread().setContextClassLoader(new ExportClassLoader());
         return client.requestEnvironment(session, id, config);
      }

      @Override
      public void disconnect(UUID sessionId) throws RemoteException {
         client.disconnect(sessionId);
      }

      @Override
      public String getHttpURL() {
         return server.getOteRestServer();
      }
      
   }
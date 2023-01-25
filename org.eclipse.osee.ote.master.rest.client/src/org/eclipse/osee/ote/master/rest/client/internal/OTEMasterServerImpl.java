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
package org.eclipse.osee.ote.master.rest.client.internal;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServer;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerAvailableNodes;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerResult;
import org.eclipse.osee.ote.master.rest.model.OTEServer;

public class OTEMasterServerImpl implements OTEMasterServer {

   static final String CONTEXT_NAME = "otemaster";
   static final String CONTEXT_SERVERS = "servers";

   private volatile JaxRsApi jaxRsApi;
   private ExecutorService executor;

   public void bindJaxRsApi(JaxRsApi jaxRsApi) {
      this.jaxRsApi = jaxRsApi;
      jaxRsApi.createClientFactory(null);
   }

   public void start(Map<String, Object> props) {
      executor = Executors.newCachedThreadPool(new ThreadFactory() {
         @Override
         public Thread newThread(Runnable arg0) {
            Thread th = new Thread(arg0);
            th.setName("OTE Master Client " + th.getId());
            th.setDaemon(false);
            return th;
         }
      });
   }

   public void stop() {
      if (executor != null) {
         executor.shutdown();
      }
      jaxRsApi = null;
   }

   @Override
   public Future<OTEMasterServerAvailableNodes> getAvailableServers(URI uri) {
      return executor.submit(new GetAvailableServers(jaxRsApi, uri));
   }

   @Override
   public Future<OTEMasterServerResult> addServer(URI uri, OTEServer server) {
      return executor.submit(new AddServer(jaxRsApi, uri, server));
   }

   @Override
   public Future<OTEMasterServerResult> removeServer(URI uri, OTEServer server) {
      return executor.submit(new RemoveServer(jaxRsApi, uri, server));
   }

}

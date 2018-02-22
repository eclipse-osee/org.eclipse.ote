/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.service.core;

import java.util.logging.Level;

import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.service.TestSessionException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class ConnectionServiceTracker extends ServiceTracker {

//   private final OteClientEndpointReceive endpointReceive = new OteClientEndpointReceive();
//   private final OteClientEndpointSend endpointSend = new OteClientEndpointSend();
//   private final MessagingGatewayBindTracker messagingGatewayTracker;

   private ServiceRegistration registration;
   private TestClientServiceImpl testClientService;

   public ConnectionServiceTracker(BundleContext context) {
      super(context, IConnectionService.class.getName(), null);
//      messagingGatewayTracker = new MessagingGatewayBindTracker(context, endpointSend, endpointReceive);
//      messagingGatewayTracker.open(true);
   }

   @Override
   public Object addingService(ServiceReference reference) {
      IConnectionService connectionService = (IConnectionService) super.addingService(reference);
      testClientService = new TestClientServiceImpl(connectionService);
      testClientService.init();
      // register the service
      registration = context.registerService(IOteClientService.class.getName(), testClientService, null);
      return connectionService;
   }

   private void shutdownClientService() {
      if (testClientService != null) {
         // we should fire off all disconnect listeners before we unregister the service
         if (testClientService.isConnected()) {
            try {
               testClientService.disconnect();
            } catch (TestSessionException ex) {
               OseeLog.log(ConnectionServiceTracker.class, Level.SEVERE, "failed to disconnect", ex);
            }
         }
         registration.unregister();
         try {
            testClientService.stop();
         } catch (Exception e) {
            OseeLog.log(ConnectionServiceTracker.class, Level.SEVERE, "failed to properly stop OTE client service", e);

         }
         testClientService = null;
      }
   }

   @Override
   public void close() {
      shutdownClientService();
//      messagingGatewayTracker.close();
      super.close();
   }

   @Override
   public void removedService(ServiceReference reference, Object service) {
      shutdownClientService();
      super.removedService(reference, service);
   }

}

/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.ote.ui.eviewer;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.eclipse.ote.ui.eviewer.view.ElementViewer;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ken J. Aguilar
 */
public class ClientMessageServiceTracker extends ServiceTracker<IOteMessageService, IOteMessageService> {

   private final ElementViewer viewer;

   @SuppressWarnings("unchecked")
   public ClientMessageServiceTracker(ElementViewer viewer) {
      super(Activator.getDefault().getBundleContext(), IOteMessageService.class.getName(), null);
      this.viewer = viewer;
   }

   @Override
   public synchronized IOteMessageService addingService(ServiceReference<IOteMessageService> reference) {
      IOteMessageService service = super.addingService(reference);
      try {
         viewer.serviceStarted(service);
      } catch (RuntimeException e) {
         OseeLog.log(ClientMessageServiceTracker.class, Level.SEVERE, "exception while notifying viewer of service", e);
      }
      return service;
   }

   @Override
   public synchronized void removedService(ServiceReference<IOteMessageService> reference, IOteMessageService service) {
      try {
         viewer.serviceStopping(service);
      } catch (RuntimeException e) {
         OseeLog.log(ClientMessageServiceTracker.class, Level.SEVERE,
            "exception while notifying viewer of service stop", e);
      }
      super.removedService(reference, service);
   }
}

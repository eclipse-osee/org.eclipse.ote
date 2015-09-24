/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

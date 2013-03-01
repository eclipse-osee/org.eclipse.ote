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
@SuppressWarnings("rawtypes")
public class ClientMessageServiceTracker extends ServiceTracker {

   private final ElementViewer viewer;

   @SuppressWarnings("unchecked")
   public ClientMessageServiceTracker(ElementViewer viewer) {
      super(Activator.getDefault().getBundleContext(), IOteMessageService.class.getName(), null);
      this.viewer = viewer;
   }

   @SuppressWarnings("unchecked")
   @Override
   public synchronized Object addingService(ServiceReference reference) {
      IOteMessageService service = (IOteMessageService) super.addingService(reference);
      try {
         viewer.serviceStarted(service);
      } catch (RuntimeException e) {
         OseeLog.log(ClientMessageServiceTracker.class, Level.SEVERE, "exception while notifying viewer of service", e);
      }
      return service;
   }

   @SuppressWarnings("unchecked")
   @Override
   public synchronized void removedService(ServiceReference reference, Object service) {
      IOteMessageService oteMessageService = (IOteMessageService) service;
      try {
         viewer.serviceStopping(oteMessageService);
      } catch (RuntimeException e) {
         OseeLog.log(ClientMessageServiceTracker.class, Level.SEVERE,
            "exception while notifying viewer of service stop", e);
      }
      super.removedService(reference, service);
   }
}

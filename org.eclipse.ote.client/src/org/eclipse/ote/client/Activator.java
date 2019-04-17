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
package org.eclipse.ote.client;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.service.IOteClientService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 */
public class Activator implements BundleActivator {

   @SuppressWarnings("rawtypes")
   private ServiceTracker oteClientServiceTracker;
   private static Activator instance = null;

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public void start(final BundleContext context) throws Exception {
      instance = this;
      oteClientServiceTracker = new ServiceTracker(context, IOteClientService.class.getName(), null);
      oteClientServiceTracker.open();
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      oteClientServiceTracker.close();
      oteClientServiceTracker = null;

      instance = null;
   }

   public static Activator getDefault() {
      return instance;
   }

   public IOteClientService getClientService() {
      return (IOteClientService) oteClientServiceTracker.getService();
   }

   public static void log(Level level, String message, Throwable t) {
      OseeLog.log(Activator.class, level, message, t);
   }

   public static void log(Level level, String message) {
      log(level, message, null);
   }
}

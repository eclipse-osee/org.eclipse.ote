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
package org.eclipse.ote.test.manager.internal;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.ui.IOteConsoleService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OteTestManagerPlugin implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.ote.test.manager";

   private static OteTestManagerPlugin plugin;

   private ServiceTracker oteClientServiceTracker;
   private ServiceTracker oteConsoleServiceTracker;

   public static OteTestManagerPlugin getInstance() {
      return plugin;
   }

   public static void log(Level level, String message) {
      log(level, message, null);
   }

   public static void log(Level level, String message, Throwable t) {
      OseeLog.log(OteTestManagerPlugin.class, level, message, t);
   }

   @Override
   public void start(BundleContext context) throws Exception {
      oteClientServiceTracker = new ServiceTracker(context, IOteClientService.class.getName(), null);
      oteClientServiceTracker.open();

      oteConsoleServiceTracker = new ServiceTracker(context, IOteConsoleService.class.getName(), null);
      oteConsoleServiceTracker.open();
      plugin = this;
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      if (oteConsoleServiceTracker != null) {
         oteConsoleServiceTracker.close();
      }
      if (oteClientServiceTracker != null) {
         oteClientServiceTracker.close();
      }
   }

   public IOteClientService getOteClientService() {
      return (IOteClientService) oteClientServiceTracker.getService();
   }

   public IOteConsoleService getOteConsoleService() {
      return (IOteConsoleService) oteConsoleServiceTracker.getService();
   }
}

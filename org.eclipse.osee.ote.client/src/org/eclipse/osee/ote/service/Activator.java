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

package org.eclipse.osee.ote.service;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.service.core.ConnectionServiceTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

   private ConnectionServiceTracker connectionServiceTracker;

   @Override
   public void start(BundleContext context) throws Exception {
      connectionServiceTracker = new ConnectionServiceTracker(context);
      connectionServiceTracker.open(true);
   }

   @Override
   public void stop(BundleContext context) throws Exception {

      // close the service tracker
      connectionServiceTracker.close();
      connectionServiceTracker = null;

   }

   public static void log(Level level, String message, Throwable t) {
      OseeLog.log(Activator.class, level, message, t);
   }

   public static void log(Level level, String message) {
      log(level, message, null);
   }
}

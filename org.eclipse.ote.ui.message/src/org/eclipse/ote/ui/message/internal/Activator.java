/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.ote.ui.message.internal;

import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.ote.service.IOteClientService;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator extends OseeUiActivator {

   private static Activator pluginInstance;
   public static final String PLUGIN_ID = "org.eclipse.ote.ui.message";
   @SuppressWarnings("rawtypes")
   private ServiceTracker oteClientServiceTracker;

   public Activator() {
      super(PLUGIN_ID);
   }

   public static Activator getDefault() {
      return pluginInstance;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      oteClientServiceTracker = new ServiceTracker(context, IOteClientService.class.getName(), null);
      oteClientServiceTracker.open();
      pluginInstance = this;
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      oteClientServiceTracker.close();
      pluginInstance = null;
   }

   public IOteClientService getOteClientService() {
      return (IOteClientService) oteClientServiceTracker.getService();
   }
}
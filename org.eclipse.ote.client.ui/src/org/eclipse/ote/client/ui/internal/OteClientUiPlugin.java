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
package org.eclipse.ote.client.ui.internal;

import java.util.logging.Level;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IWorkbenchUserService;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.ui.IOteConsoleService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OteClientUiPlugin extends AbstractUIPlugin {

   // The plug-in ID
   public static final String PLUGIN_ID = "org.eclipse.ote.client.ui";

   // The shared instance
   private static OteClientUiPlugin plugin;

   private ServiceTracker<IOteClientService, IOteClientService> oteClientServiceTracker;
   private ServiceTracker<IOteConsoleService, IOteConsoleService> oteConsoleServiceTracker;
   private ServiceTracker<IWorkbenchUserService, IWorkbenchUserService> workbenchUserServiceTracker;
   private BundleContext context;

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      plugin = this;
      this.context = context;

      oteClientServiceTracker = new ServiceTracker<IOteClientService, IOteClientService>(context, IOteClientService.class.getName(), null);
      oteClientServiceTracker.open();

      workbenchUserServiceTracker = new ServiceTracker<IWorkbenchUserService, IWorkbenchUserService>(context, IWorkbenchUserService.class.getName(), null);
      workbenchUserServiceTracker.open();

      oteConsoleServiceTracker = new ServiceTracker<IOteConsoleService, IOteConsoleService>(context, IOteConsoleService.class.getName(), null);
      oteConsoleServiceTracker.open();
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      if (oteClientServiceTracker != null) {
         oteClientServiceTracker.close();
      }
      if (workbenchUserServiceTracker != null) {
         workbenchUserServiceTracker.close();
      }
      if (oteConsoleServiceTracker != null) {
         oteConsoleServiceTracker.close();
      }
      this.context = null;
      plugin = null;
      super.stop(context);
   }

   /**
    * Returns the shared instance
    *
    * @return the shared instance
    */
   public static OteClientUiPlugin getDefault() {
      return plugin;
   }

   /**
    * Returns an image descriptor for the image file at the given plug-in relative path
    *
    * @param path the path
    * @return the image descriptor
    */
   public static ImageDescriptor getImageDescriptor(String path) {
      return imageDescriptorFromPlugin(PLUGIN_ID, path);
   }

   public IOteClientService getOteClientService() {
      return oteClientServiceTracker.getService();
   }

   public static void log(Level level, String message) {
      log(level, message, null);
   }

   public static void log(Level level, String message, Throwable t) {
      OseeLog.log(OteClientUiPlugin.class, level, message, t);
   }

   public IOteConsoleService[] getConsole() {
	  Object[] objs = oteConsoleServiceTracker.getServices();
	  IOteConsoleService[] services = new IOteConsoleService[objs.length];
	  for(int i = 0; i < services.length; i++){//service:services){
		  services[i] = (IOteConsoleService)objs[i];  
	  }
      return services; 
   }

   public BundleContext getBundleContext() {
      return context;
   }

   public IWorkbenchUserService getDirectoryService() {
      return workbenchUserServiceTracker.getService();
   }
}

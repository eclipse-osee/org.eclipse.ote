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

package org.eclipse.osee.ote.connection.jini;

import java.util.logging.Level;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

   // The plug-in ID
   public static final String PLUGIN_ID = "org.eclipse.osee.ote.connection.jini";

   // The shared instance
   private static Activator plugin;


   private ExportClassLoader exportClassLoader;

   /**
    * The constructor
    */
   public Activator() {
   }

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      plugin = this;

   }

   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      exportClassLoader = null;
      plugin = null;
   }

   /**
    * Returns the shared instance
    * 
    * @return the shared instance
    */
   public static Activator getDefault() {
      return plugin;
   }

   ClassLoader getExportClassLoader() {
      return exportClassLoader;
   }

   public static void log(Level level, String message, Throwable t) {
      OseeLog.log(Activator.class, level, message, t);
   }

   public static void log(Level level, String message) {
      log(level, message, null);
   }
}

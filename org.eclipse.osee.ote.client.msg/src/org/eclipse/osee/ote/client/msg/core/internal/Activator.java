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
package org.eclipse.osee.ote.client.msg.core.internal;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

   // The plug-in ID
   public static final String PLUGIN_ID = "org.eclipse.ote.client.msg";

   // The shared instance
   private static Activator plugin;

   private BundleContext context;

//   private OteClientServiceTracker tracker;

   /**
    * The constructor
    */
   public Activator() {
   }

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      this.context = context;
      plugin = this;

//      ExtensionDefinedObjects<IMessageDbFactory> definedObjects =
//         new ExtensionDefinedObjects<IMessageDbFactory>("org.eclipse.osee.ote.client.msg.dBFactory", "DatabaseFactory",
//            "className");
//      try {
//         List<IMessageDbFactory> providers = definedObjects.getObjects();
//         if (!providers.isEmpty()) {
//            tracker = new OteClientServiceTracker(providers.get(0));
//            tracker.open(true);
//         } else {
//            OseeLog.log(Activator.class, Level.WARNING,
//               "No message database factory (IMessageDbFactory) found. Message Subscription Service not started");
//         }
//      } catch (Exception ex) {
//         OseeLog.log(Activator.class, Level.SEVERE, "failed to process message database factory extensions", ex);
//      }

   }

   @Override
   public void stop(BundleContext context) throws Exception {
      plugin = null;
//      tracker.close();
      super.stop(context);
      this.context = null;
   }

   /**
    * Returns the shared instance
    *
    * @return the shared instance
    */
   public static Activator getDefault() {
      return plugin;
   }

   BundleContext getBundleContext() {
      return context;
   }

}

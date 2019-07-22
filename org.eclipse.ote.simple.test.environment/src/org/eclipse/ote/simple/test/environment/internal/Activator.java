/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.ote.simple.test.environment.internal;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * @author Andy Jury
 * 
 *         The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

   public static final String PLUGIN_ID = "org.eclipse.ote.simple.test.environment";

   private static Activator plugin;

   public Activator() {
   }

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      plugin = this;
   }

   @Override
   public void stop(BundleContext context) throws Exception {
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

}

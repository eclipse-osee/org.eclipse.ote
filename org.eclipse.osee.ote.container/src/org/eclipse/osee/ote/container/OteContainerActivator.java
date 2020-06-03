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

package org.eclipse.osee.ote.container;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.osee.ote.runtimemanager.OteUserLibsNature;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class OteContainerActivator implements BundleActivator {

   // The plug-in ID
   public static final String PLUGIN_ID = "org.eclipse.osee.ote.container";

   // The shared instance
   private static OteContainerActivator plugin;

   private BundleContext context;

   private JarChangeResourceListener<OteUserLibsNature> userLibResourceListener;

   private LibraryChangeProvider<OteUserLibsNature> userLibListener;

   /**
    * The constructor
    */
   public OteContainerActivator() {
   }

   @Override
   public void start(BundleContext context) throws Exception {
      plugin = this;
      this.context = context;
      userLibListener = new LibraryChangeProvider<>();
      this.userLibResourceListener =
         new JarChangeResourceListener<OteUserLibsNature>(OteUserLibsNature.NATURE_ID, userLibListener);

      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      workspace.addResourceChangeListener(userLibResourceListener);
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      workspace.removeResourceChangeListener(userLibResourceListener);
      context = null;
      plugin = null;
   }

   /**
    * Returns the shared instance
    * 
    * @return the shared instance
    */
   public static OteContainerActivator getDefault() {
      return plugin;
   }

   /**
    * @return the context
    */
   public BundleContext getContext() {
      return context;
   }

   public LibraryChangeProvider<OteUserLibsNature> getLibraryChangeProvider() {
      return userLibListener;
   }

}

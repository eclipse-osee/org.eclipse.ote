/*********************************************************************
 * Copyright (c) 2024 Boeing
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.ote.cat.plugin;

import java.util.Objects;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * An extension of the {@link AbstractUIPlugin} for the CAT Plug-in. This plug-in provides the ability to configure the
 * CAT annotation processor, select the projects the CAT will be applied to, and to load PLE Configurations from an OPLE
 * server.
 * 
 * @author Loren K. Ashley
 */

public class CatPlugin extends AbstractUIPlugin {

   /**
    * Saves the single instance of the {@link CatPlugin} class.
    * 
    * @implNote The static <code>instance</code> variable is expected to never be <code>null</code> on access. To access
    * the <code>instance</code> variable will cause the {@link CatPlugin} class to be loaded. As part of loading the
    * class, OSGi will instantiate the class. Thus for any access to the {@link CatPlugin} class the
    * <code>instance</code> variable is expected to be set.
    */

   private static CatPlugin instance = null;

   /**
    * Gets the {@link CatPlugin} OSGi bundle symbolic name as the identifier.
    * 
    * @return an identification string for the {@link CatPlugin}.
    */

   public static String getIdentifier() {
      assert Objects.nonNull(CatPlugin.instance) : "CatPlugin instance is unexpectedly null.";
      return CatPlugin.instance.catPluginIdentifier;
   }

   /**
    * Gets the expected OSGi extension identifier for the extension that implements the
    * "org.eclipse.core.runtime.preferences" extension point of the plug-in.
    * 
    * @return the extension identifier.
    */

   public static String getDefaultPreferenceInitializerExtensionIdentifier() {
      return CatPlugin.getIdentifier() + ".defaultpreferenceinitializer";
   }

   /**
    * Gets the {@IPreferenceStore} implementation of the {@link CatPlugin}.
    * 
    * @return the {@link CatPlugin} {@link IPreferenceStore} instance.
    */

   public static IPreferenceStore getInstancePreferenceStore() {
      assert Objects.nonNull(CatPlugin.instance) : "CatPlugin instance is unexpectedly null.";
      return CatPlugin.instance.getPreferenceStore();
   }

   /**
    * Saves the {@link CatPlugin} OSGi bundle symbolic name set in the MANIFEST.MF as the plug-in identifier. This
    * member will remain <code>null</code> when the bundle symbolic name cannot be determined.
    * 
    * @implNote This member is "lazy loaded" by the static method {@link CatPlugin#getIdentifier}.
    */

   private String catPluginIdentifier;

   /**
    * Creates an instance of the {@link CatPlugin} and sets the static instance reference.
    * 
    * @implNote OSGi encourages private members of plug-in objects to be lazy loaded. All members are set to
    * <code>null</code> as a sentinel value indicating the members need to be loaded.
    */

   public CatPlugin() {
      super();
      CatPlugin.instance = this;
      Bundle bundle = this.getBundle();
      this.catPluginIdentifier = bundle.getSymbolicName();
   }

   /**
    * Bundle activator method for the {@link CatPlugin}. The {@link AbstractUIPlugin#start} method is called to enable
    * the base class features for the plug-in.
    * <p>
    * {@inheritDoc}
    * 
    * @throws CatPluginException when the bundle fails to start.
    */

   @Override
   public void start(BundleContext context) throws Exception {

      try {

         super.start(context);

      } catch (Exception e) {

         //@formatter:off
         CatPluginException startException =
            new CatPluginException
                   (
                      StatusManager.LOG,
                      "CAT Plugin Activator Error",
                      Status.ERROR,
                      "CAT Plugin Activator failed to start.",
                      e
                   );
         //@formatter:on
         startException.log();

         throw startException;
      }

   }

   /**
    * Bundle deactivator method for the {@link CatPlugin}. The {@link AbstractUIPlugin#stop} method is called to provide
    * an orderly shut down of the plug-in.
    * <p>
    * {@inheritDoc}
    * 
    * @throws CatPluginException when the bundle fails to stop.
    */

   @Override
   public void stop(BundleContext context) throws Exception {

      try {

         super.stop(context);

      } catch (Exception e) {

         //@formatter:off
         CatPluginException stopException =
            new CatPluginException
                   (
                      StatusManager.LOG,
                      "CAT Plugin Activator Error",
                      Status.ERROR,
                      "CAT Plugin Activator failed to stop.",
                      e
                   );
         //@formatter:on
         stopException.log();

         throw stopException;
      }
   }

}

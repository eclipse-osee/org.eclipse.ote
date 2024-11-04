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

import java.io.File;
import java.util.Objects;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.Policy;
import org.eclipse.ote.cat.plugin.exception.CatErrorCode;
import org.eclipse.ote.cat.plugin.exception.CatErrorSupportProvider;
import org.eclipse.ote.cat.plugin.exception.CatPluginException;
import org.eclipse.ote.cat.plugin.project.CatProjectManager;
import org.eclipse.ote.cat.plugin.util.Extensions;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

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
    * Finds the identifier of the CAT Plug-In extension for the extension point
    * {@value Constants#naturesExtensionPointIdentifier} from the &quot;plugin.xml&quot; manifest file.
    * 
    * @return the full extension identifier with name space.
    * @throws CatPluginException when an extension is not found or more than one extension is found for the extension
    * point.
    */

   private static String findCatNatureIdentifier() {

      try {
         IExtension extension =
            Extensions.getExtensions(Constants.naturesExtensionPointIdentifier, CatPlugin.getIdentifier(), 1).get(0);

         String natureIdentifier = extension.getUniqueIdentifier();

         return natureIdentifier;
      } catch (Exception e) {
         //@formatter:off
         CatPluginException findCatNatureIdentifierException =
            new CatPluginException
                   (
                      CatErrorCode.InternalError,
                      "Failed to get the \"CatNature\" identifier from the \"plugin.xml\" manifest."
                   );
         //@formatter:on
         throw findCatNatureIdentifierException;
      }
   }

   /**
    * Finds the command line option name for the default preferences file in the &quot;plugin.xml&quot; file. The
    * command line option is specified with the {@value Constants#preferencesCommandLineOptionAttributeName} attribute
    * of the configuration element {@value Constants#preferencesInitializerConfigurationElement}; of the extension for
    * the extension point {@value Constants#preferencesExtensionPointIdentifier}.
    * 
    * @return the command line option extension name for the default preferences file.
    * @throws CatPluginException when an extension is not found or more than one extension is found for the extension
    * point; the &quot;initializer&quot; configuration element is not found; or the &quot;option&quot; attribute is not
    * found.
    */

   private static String findDefaultPreferencesCommandLineOptionName() {

      try {
         IExtension extension =
            Extensions.getExtensions(Constants.preferencesExtensionPointIdentifier, CatPlugin.getIdentifier(), 1).get(
               0);

         IConfigurationElement configurationElement =
            Extensions.getConfigurationElements(extension, Constants.preferencesInitializerConfigurationElement, 1).get(
               0);

         String option =
            Extensions.getAttribute(configurationElement, Constants.preferencesCommandLineOptionAttributeName);

         return option;
      } catch (Exception e) {
         //@formatter:off
         CatPluginException findDefaultPreferencesCommandLineOptionNameException =
            new CatPluginException
                   (
                      CatErrorCode.InternalError,
                      "Failed to get the command line option name for the default preferences file from the \"plugin.xml\" manifest."
                   );
         //@formatter:on
         throw findDefaultPreferencesCommandLineOptionNameException;
      }
   }

   /**
    * Gets the extension identifier of the CAT Plug-In project nature.
    * 
    * @return the CAT Plug-In project nature identifier.
    */

   public static String getCatNatureIdentifier() {
      assert Objects.nonNull(CatPlugin.instance) : "CatPlugin instance is unexpectedly null.";
      return CatPlugin.instance.catNatureIdentifier;
   }

   /**
    * Gets the {@link CatProjectManager} instance. The instance is created when the plug-in is started and
    * <code>null</code>ed when the plug-in is stopped.
    * 
    * @return the {@link CatProjectManager} instance.
    */

   public static CatProjectManager getCatProjectManager() {
      assert Objects.nonNull(CatPlugin.instance) : "CatPlugin instance is unexpectedly null.";
      return CatPlugin.instance.catProjectManager;
   }

   /**
    * Gets the command line option name for the default preferences file.
    * 
    * @return the command line option name.
    */

   public static String getDefaultPreferencesCommandLineOptionName() {
      assert Objects.nonNull(CatPlugin.instance) : "CatPlugin instance is unexpectedly null.";
      return CatPlugin.instance.defaultPreferencesCommandLineOptionName;
   }

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
    * Gets the {@IPreferenceStore} implementation of the {@link CatPlugin}.
    * 
    * @return the {@link CatPlugin} {@link IPreferenceStore} instance.
    */

   public static IPreferenceStore getInstancePreferenceStore() {
      assert Objects.nonNull(CatPlugin.instance) : "CatPlugin instance is unexpectedly null.";
      return CatPlugin.instance.getPreferenceStore();
   }

   /**
    * Gets a {@link File} handle for the plug-in state file. The state file is used to persist this
    * {@link ProjectManager} cache. The file pointed to by the handle may not exist. This method will create the state
    * location folder if it does not exist.
    * 
    * @return a {@link File} handle for the plug-in state file.
    */

   public static File getStateLocationFile() {
      File stateLocationFile = CatPlugin.instance.getStateLocation().append(Constants.catPluginStateFile).toFile();
      return stateLocationFile;
   }

   /**
    * Saves the CAT Plug-In's preferences to file.
    * 
    * @throws CatPluginException when unable to persist the preference store.
    */

   public static void savePreferences() {
      assert Objects.nonNull(CatPlugin.instance) : "CatPlugin instance is unexpectedly null.";
      try {
         InstanceScope.INSTANCE.getNode(CatPlugin.getIdentifier()).flush();
      } catch (BackingStoreException e) {
         //@formatter:off
         CatPluginException preferenceFileSaveException =
            new CatPluginException
                   (
                      CatErrorCode.PreferenceFileSaveError,
                      "Failed to save the CAT Plug-In preferences file.",
                      e
                   );
         //@formatter:on
         throw preferenceFileSaveException;
      } catch (Exception e) {
         //@formatter:off
         CatPluginException preferenceFileSaveException =
            new CatPluginException
                   (
                      CatErrorCode.PreferenceFileSaveError,
                      "Failed to save the CAT Plug-In preferences file."
                   );
         //@formatter:on
         throw preferenceFileSaveException;
      }
   }

   /**
    * Saves the extension identifier for the CAT Plug-In project nature.
    */

   private final String catNatureIdentifier;

   /**
    * Saves the {@link CatPlugin} OSGi bundle symbolic name set in the MANIFEST.MF as the plug-in identifier.
    */

   private final String catPluginIdentifier;

   /**
    * Saves the {@link CatProjectManager} instance.
    */

   private final CatProjectManager catProjectManager;

   /**
    * Saves the command line option name used for the default preferences file.
    */

   private final String defaultPreferencesCommandLineOptionName;

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
      this.catNatureIdentifier = CatPlugin.findCatNatureIdentifier();
      this.defaultPreferencesCommandLineOptionName = CatPlugin.findDefaultPreferencesCommandLineOptionName();
      this.catProjectManager = new CatProjectManager();
   }

   /**
    * Bundle activation method for the {@link CatPlugin}. The {@link AbstractUIPlugin#start} method is called to enable
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
         Policy.setErrorSupportProvider(new CatErrorSupportProvider());
         this.catProjectManager.start();
         CatErrorCode.verifyStatusCodes();

      } catch (CatPluginException cpe) {
         /*
          * Internal exceptions are caught and re-thrown to prevent them from getting wrapped into another
          * CatPluginException.
          */
         throw cpe;
      } catch (Exception e) {

         //@formatter:off
         CatPluginException startException =
            new CatPluginException
                   (
                      CatErrorCode.InternalError,
                      "CAT Plugin Activator failed to start.",
                      e
                   );
         //@formatter:on

         throw startException;
      }

   }

   /**
    * Bundle deactivation method for the {@link CatPlugin}. The {@link AbstractUIPlugin#stop} method is called to
    * provide an orderly shut down of the plug-in.
    * <p>
    * {@inheritDoc}
    * 
    * @throws CatPluginException when the bundle fails to stop.
    */

   @Override
   public void stop(BundleContext context) throws Exception {

      try {

         this.catProjectManager.stop();
         super.stop(context);

      } catch (Exception e) {

         //@formatter:off
         CatPluginException stopException =
            new CatPluginException
                   (
                      CatErrorCode.InternalError,
                      "CAT Plugin Activator failed to stop.",
                      e
                   );
         //@formatter:on
         stopException.log();

         throw stopException;
      }
   }

}

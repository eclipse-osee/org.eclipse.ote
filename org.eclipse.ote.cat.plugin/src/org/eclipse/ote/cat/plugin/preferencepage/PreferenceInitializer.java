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

package org.eclipse.ote.cat.plugin.preferencepage;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ote.cat.plugin.CatPlugin;
import org.eclipse.ote.cat.plugin.CatPluginException;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * An extension of the {@link AbstractPreferenceInitializer} used to provide default preference values from a JSON file
 * specified by the command line option {@value PreferenceInitializer#commandLineOption}. This method is invoked by the
 * Eclipse framework only when a default preference value is needed.
 * 
 * @author Loren K. Ashley
 */

public class PreferenceInitializer extends AbstractPreferenceInitializer {

   /**
    * The name of the attribute in the OSGi Configuration Element that specifies the name of the command line option for
    * the default preferences file.
    */

   private static final String commandLineOptionNameAttribute = "option";

   /**
    * The name of the OSGi Configuration Element that specifies this class as the default value initializer.
    */

   private static final String configurationElementName = "initializer";

   /**
    * The dialog box title used for status dialogs.
    */

   private static final String exceptionTitle = "CAT Plugin Default Preferences";

   /**
    * The name of the OSGi Extension Point implemented by this class.
    */

   private static final String extensionPointName = "org.eclipse.core.runtime.preferences";

   /**
    * Searches the application command line options for one starting with the string <code>commandLineOptionName</code>.
    * 
    * @param commandLineOptionName the name of the command line option to search for.
    * @return the first found matching command line option.
    * @throws CatPluginException when the command line option is not present.
    */

   private static String getCommandLineOption(String commandLineOptionName) {

      String[] arguments = Platform.getApplicationArgs();

      int i;

      for (i = 0; i < arguments.length; i++) {

         if (arguments[i].startsWith(commandLineOptionName)) {
            return arguments[i];
         }

      }

      //@formatter:off
      CatPluginException commandLineOptionNotPresent =
         new CatPluginException
                (
                   StatusManager.LOG,
                   PreferenceInitializer.exceptionTitle,
                   IStatus.INFO,
                     "The command line option \"" + commandLineOptionName + "\" was not specified." + "\n"
                   + "Default preference values for the CAT Plugin were not loaded."                + "\n",
                   null
                );
      //@formatter:on
      throw commandLineOptionNotPresent;
   }

   /**
    * Obtains the default preferences file command line option name from the CAT Plugin &quot;plugin.xml&quot; file. The
    * command line option is specified by the {@value #commandLineOptionNameAttribute} attribute of the Configuration
    * Element {@value #configurationElementName} of the Extension element for the Extension Point
    * {@value #extensionPointName}.
    * 
    * @return the command line option name for the CAT Plugin default preferences file.
    * @throws CatPluginException when unable to determine the command line option name.
    */

   private static String getCommandLineOptionName() {

      String defaultPreferenceInitializerExtensionIdentifier =
         CatPlugin.getDefaultPreferenceInitializerExtensionIdentifier();

      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

      if (Objects.isNull(extensionRegistry)) {
         //@formatter:off
         CatPluginException osgiExtensionRegistryNotAvailable =
            new CatPluginException
                   (
                      StatusManager.LOG,
                      PreferenceInitializer.exceptionTitle,
                      IStatus.ERROR,
                        "The OSGi Extension Registry is not available. Unable to determine the command line option" + "\n"
                      + "name for the CAT Plugin default preferences file."                                         + "\n",
                      null
                   );
         //@formatter:on
         throw osgiExtensionRegistryNotAvailable;
      }

      IExtensionPoint extensionPoint = extensionRegistry.getExtensionPoint(PreferenceInitializer.extensionPointName);

      if (Objects.isNull(extensionPoint)) {
         //@formatter:off
         CatPluginException osgiExtensionPointNotFound =
            new CatPluginException
                   (
                      StatusManager.LOG,
                      PreferenceInitializer.exceptionTitle,
                      IStatus.ERROR,
                        "The OSGi Extension Point \"" + PreferenceInitializer.extensionPointName + "\" was not found."  + "\n" 
                      + "Unable to determine the command line option name for the CAT Plugin default preferences file." + "\n",
                      null
                   );
         //@formatter:on
         throw osgiExtensionPointNotFound;
      }

      IExtension extension = null;
      Exception extensionCause = null;

      try {
         extension = extensionPoint.getExtension(defaultPreferenceInitializerExtensionIdentifier);
      } catch (Exception e) {
         extensionCause = e;
      }

      if (Objects.isNull(extension)) {
         //@formatter:off
         CatPluginException osgiExtensionNotFound =
            new CatPluginException
                   (
                      StatusManager.LOG,
                      PreferenceInitializer.exceptionTitle,
                      IStatus.ERROR,
                        "The OSGi Extension \"" + defaultPreferenceInitializerExtensionIdentifier + "\" was not found."  + "\n" 
                      + "Unable to determine the command line option name for the CAT Plugin default preferences file."  + "\n",
                      extensionCause // <- might be null
                   );
         //@formatter:on
         throw osgiExtensionNotFound;
      }

      IConfigurationElement[] configurationElements = null;
      Exception configurationElementsCause = null;

      try {
         configurationElements = extension.getConfigurationElements();
      } catch (Exception e) {
         configurationElementsCause = e;
      }

      if (Objects.isNull(extension)) {
         //@formatter:off
         CatPluginException osgiConfigurationElementsNotFound =
            new CatPluginException
                   (
                      StatusManager.LOG,
                      PreferenceInitializer.exceptionTitle,
                      IStatus.ERROR,
                        "The OSGi Configuration Elments of the Extension \"" + defaultPreferenceInitializerExtensionIdentifier + "\""    + "\n" 
                      + "were not found. Unable to determine the command line option name for the CAT Plugin default preferences file."  + "\n",
                      configurationElementsCause
                   );
         //@formatter:on
         throw osgiConfigurationElementsNotFound;
      }

      IConfigurationElement configurationElement = null;
      Exception configurationElementCause = null;

      try {
         for (int i = 0; i < configurationElements.length; i++) {
            if (Objects.nonNull(configurationElements[i]) && PreferenceInitializer.configurationElementName.equals(
               configurationElements[i].getName())) {
               configurationElement = configurationElements[i];
               break;
            }
         }
      } catch (Exception e) {
         configurationElementCause = e;
      }

      if (Objects.isNull(configurationElement)) {
         //@formatter:off
         CatPluginException cannotFindInitializerConfigurationElement =
            new CatPluginException
                   (
                      StatusManager.LOG,
                      PreferenceInitializer.exceptionTitle,
                      IStatus.INFO,
                        "The command line option used to specify the default preference values file cannot be determined."    + "\n"
                      + "Ensure the \"initializer\" element for the extension point \"org.eclipse.core.runtime.preferences\"" + "\n"
                      + "is specified in the \"plugin.xml\" file of the CAT Plugin."                                          + "\n",
                      configurationElementCause // <- might be null
                   );
         //@formatter:on
         throw cannotFindInitializerConfigurationElement;
      }

      String option = null;
      Exception optionCause = null;

      try {
         option = configurationElement.getAttribute(PreferenceInitializer.commandLineOptionNameAttribute);
      } catch (Exception e) {
         optionCause = e;
      }

      if (Objects.isNull(option)) {
         //@formatter:off
         CatPluginException cannotDetermineCommandLineOptionNameForDefaults =
            new CatPluginException
                   (
                      StatusManager.LOG,
                      PreferenceInitializer.exceptionTitle,
                      IStatus.INFO,
                        "The command line option used to specify the default preference values file cannot be determined." + "\n"
                      + "Ensure the attribute \"option\" of the \"initializer\" element for the extension point"           + "\n" 
                      + "\"org.eclipse.core.runtime.preferences\" is set in the \"plugin.xml\" file of the CAT Plugin."    + "\n",
                      optionCause // <- might be null
                   );
         //@formatter:on
         throw cannotDetermineCommandLineOptionNameForDefaults;
      }

      return option;
   }

   /**
    * Parses the default preferences file path from the command line option.
    * 
    * @param commandLineOptionName the name of the command line option.
    * @param commandLineOption the command line option and value from the application.
    * @return the command line option value
    * @throws CatPluginException when unable to parse the value from the <code>commandLineOption</code>.
    */

   private static String getCommandLineOptionValue(String commandLineOptionName, String commandLineOption) {

      int p = commandLineOption.indexOf('=');
      int pMax = commandLineOption.length() - 1;

      if ((p < 0) || (p >= pMax)) {
         //@formatter:off
         CatPluginException noOptionValueException =
            new CatPluginException
                   (
                      StatusManager.LOG,
                      PreferenceInitializer.exceptionTitle,
                      IStatus.WARNING,
                      "A value was not specified for the command line option \"" + commandLineOptionName + "\"." + "\n",
                      null
                   );
         //@formatter:on
         throw noOptionValueException;
      }

      p++;

      String value = commandLineOption.substring(p);

      return value;

   }

   /**
    * Creates the default preference initializer.
    * 
    * @implNote This class is not instantiated unless a default value is requested from the CAT Plugin preference store.
    */

   public PreferenceInitializer() {
      super();
   }

   /**
    * When the default preferences file command line option is present and the specified JSON file is successfully
    * parsed, the CAT Plugin preference store default values are set with those from the default preferences file.
    * 
    * @throws CatPluginException when:
    * <ul>
    * <li>unable to determine the name of the default preferences file command line option,</li>
    * <li>unable to parse the command line option,</li>
    * <li>unable to read the default preferences file,</li>
    * <li>unable to parse the default preferences file, or</li>
    * <li>a default preference cannot be validated.</li>
    * </ul>
    * @implNote This method is only called by the Eclipse framework when a default value is requested from the CAT
    * Plugin preference store.
    */

   @Override
   public void initializeDefaultPreferences() {

      String commandLineOptionName = null;
      String commandLineOption = null;
      String commandLineOptionValue = null;
      try {
         commandLineOptionName = PreferenceInitializer.getCommandLineOptionName();
         commandLineOption = PreferenceInitializer.getCommandLineOption(commandLineOptionName);
         commandLineOptionValue =
            PreferenceInitializer.getCommandLineOptionValue(commandLineOptionName, commandLineOption);

         File file = null;
         Exception fileException = null;

         try {

            Path filePath = Paths.get(commandLineOptionValue);
            file = filePath.toFile();

            if (!file.canRead()) {
            //@formatter:off
            fileException =
               new CatPluginException
                      (
                         StatusManager.BLOCK | StatusManager.LOG,
                         PreferenceInitializer.exceptionTitle,
                         IStatus.ERROR,
                           "The CAT Plugin default preferences file does not exsit or cannot be read." + "\n"
                         + "   Default Preferences File: " + commandLineOptionValue                    + "\n"
                         + "   Command Line Option:      " + commandLineOptionName                     + "\n",
                         null
                      );
            //@formatter:on
            }

         } catch (Exception e) {
            fileException = e;
         }

         if (Objects.nonNull(fileException)) {
            if (fileException instanceof CatPluginException) {
               throw fileException;
            } else {
            //@formatter:off
            CatPluginException systemFileException =
               new CatPluginException
                      (
                         StatusManager.BLOCK | StatusManager.LOG,
                         PreferenceInitializer.exceptionTitle,
                         IStatus.ERROR,
                           "An error occurred testing the accessability of the CAT Plugin default preferences file." + "\n"
                         + "   Default Preferences File: " + commandLineOptionValue                                  + "\n"
                         + "   Command Line Option:      " + commandLineOptionName                                   + "\n",
                         fileException
                      );
            //@formatter:on
               throw systemFileException;
            }
         }

         DefaultPreferences defaultPreferences = null;
         Exception defaultPreferencesException = null;

         try {
            ObjectMapper objectMapper = new ObjectMapper();
            defaultPreferences = objectMapper.readValue(file, DefaultPreferences.class);
         } catch (Exception e) {
            defaultPreferencesException = null;
         }

         if (Objects.isNull(defaultPreferences)) {
         //@formatter:off
         CatPluginException failedToReadDefaultPreferencesException =
            new CatPluginException
                   (
                      StatusManager.BLOCK | StatusManager.LOG,
                      PreferenceInitializer.exceptionTitle,
                      IStatus.WARNING,
                        "Failed to read the CAT Plugin default preferences file." + "\n"
                      + "   Default Preferences File: " + commandLineOptionValue  + "\n"
                      + "   Command Line Option:      " + commandLineOptionName   + "\n",
                      defaultPreferencesException // <- might be null
                   );
         //@formatter:on
            throw failedToReadDefaultPreferencesException;
         }

         IPreferenceStore preferenceStore = CatPlugin.getInstancePreferenceStore();

         for (Preference preference : Preference.values()) {
            //@formatter:off
            preference
               .getDefault( defaultPreferences )
               .ifPresent
                  ( 
                     ( defaultValue ) -> preferenceStore.setDefault
                                            ( 
                                               preference.getPreferenceStoreName(),
                                               defaultValue 
                                            )
                  );
            //@formatter:on
         }

      } catch (CatPluginException catPluginException) {
         catPluginException.log();
      } catch (Exception e) {
         //@formatter:off
         CatPluginException setDefaultValuesException =
            new CatPluginException
                   (
                      StatusManager.BLOCK | StatusManager.LOG,
                      PreferenceInitializer.exceptionTitle,
                      IStatus.ERROR,
                        "Failed to set a CAT Plugin default preference value."    + "\n"
                      + "   Default Preferences File: " + commandLineOptionValue  + "\n"
                      + "   Command Line Option:      " + commandLineOptionName   + "\n",
                      e
                   );
         //@formatter:on
         setDefaultValuesException.log();
      }

   }

}

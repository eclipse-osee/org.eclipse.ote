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

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ote.cat.plugin.CatPlugin;
import org.eclipse.ote.cat.plugin.exception.CatErrorCode;
import org.eclipse.ote.cat.plugin.exception.CatPluginException;

/**
 * An extension of the {@link AbstractPreferenceInitializer} used to provide default preference values from a JSON file
 * specified by the command line option {@value PreferenceInitializer#commandLineOption}. This method is invoked by the
 * Eclipse framework only when a default preference value is needed.
 * 
 * @author Loren K. Ashley
 */

public class PreferenceInitializer extends AbstractPreferenceInitializer {

   /**
    * Searches the application command line options for one starting with the string <code>commandLineOptionName</code>.
    * 
    * @param commandLineOptionName the name of the command line option to search for.
    * @return the first found matching command line option.
    * @throws CatPluginUserException when the command line option is not present.
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
                   CatErrorCode.NoDefaultPreferencesWarning,
                     "The command line option \"" + commandLineOptionName + "\" was not specified." + "\n"
                   + "Default preference values for the CAT Plugin were not loaded."                + "\n"
                );
      //@formatter:on
      throw commandLineOptionNotPresent;
   }

   /**
    * Parses the default preferences file path from the command line option.
    * 
    * @param commandLineOptionName the name of the command line option.
    * @param commandLineOption the command line option and value from the application.
    * @return the command line option value
    * @throws CatPluginUserException when unable to parse the value from the <code>commandLineOption</code>.
    */

   private static String getCommandLineOptionValue(String commandLineOptionName, String commandLineOption) {

      int p = commandLineOption.indexOf('=');
      int pMax = commandLineOption.length() - 1;

      if ((p < 0) || (p >= pMax)) {
         //@formatter:off
         CatPluginException noOptionValueException =
            new CatPluginException
                   (
                      CatErrorCode.CommandLineOptionError,
                      "A value was not specified for the command line option \"" + commandLineOptionName + "\"." + "\n"
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
    * parsed, the CAT Plug-In preference store default values are set with those from the default preferences file.
    * 
    * @throws CatPluginUserException when:
    * <ul>
    * <li>unable to determine the name of the default preferences file command line option,</li>
    * <li>unable to parse the command line option,</li>
    * <li>unable to read the default preferences file,</li>
    * <li>unable to parse the default preferences file, or</li>
    * <li>a default preference cannot be validated.</li>
    * </ul>
    * @implNote This method is only called by the Eclipse framework when a default value is requested from the CAT
    * Plug-In preference store.
    */

   @Override
   public void initializeDefaultPreferences() {

      String commandLineOptionName = null;
      String commandLineOption = null;
      String commandLineOptionValue = null;
      try {
         commandLineOptionName = CatPlugin.getDefaultPreferencesCommandLineOptionName();
         commandLineOption = PreferenceInitializer.getCommandLineOption(commandLineOptionName);
         commandLineOptionValue =
            PreferenceInitializer.getCommandLineOptionValue(commandLineOptionName, commandLineOption);

         CatPreferences catPreferences = CatPreferences.read(commandLineOptionValue);

         IPreferenceStore preferenceStore = CatPlugin.getInstancePreferenceStore();

         for (Preference preference : Preference.values()) {
            //@formatter:off
            preference
               .getDefault( catPreferences )
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
                      CatErrorCode.PreferenceFileError,
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

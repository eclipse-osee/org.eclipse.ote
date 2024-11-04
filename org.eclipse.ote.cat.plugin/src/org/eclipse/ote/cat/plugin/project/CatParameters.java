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

package org.eclipse.ote.cat.plugin.project;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.ote.cat.plugin.Constants;
import org.eclipse.ote.cat.plugin.preferencepage.Preference;

/**
 * A container class for the CAT Plug-In preferences needed to configure the compiler options for a project.
 * 
 * @author Loren K. Ashley
 */

public class CatParameters {

   /**
    * Gets the CAT annotation processor option name used to specify the path of the PLE Configuration file.
    * 
    * @return the PLE Configuration path option name.
    */

   public static String getPleConfigurationPathKey() {
      return Constants.pleConfigurationPathCatOption;
   }

   /**
    * Gets the CAT annotation processor option name used to specify the source location method.
    * 
    * @return the source location method option name.
    */

   public static String getSourceLocationMethodKey() {
      return Constants.sourceLocationMethodCatOption;
   }

   /**
    * Save the path to the CAT annotation processor Jar file. This parameter is used to configure the project's factory
    * path.
    */

   private final Path catJarPath;

   /**
    * Saves the path to the PLE Configuration file to be used by the CAT annotation processor. This parameter is used to
    * configure the project's annotation processor parameters.
    */

   private final Path pleConfigurationPath;

   /**
    * Saves the method to be used by the CAT annotation processor to locate source files. This parameter is used to
    * configure the project's annotation processor parameters.
    */

   private final String sourceLocationMethod;

   /**
    * Creates a new immutable {@link CatParameters} object with the preference values needed to configure the compiler
    * options for a project.
    * <h3>See Also:</h3>
    * <ul>
    * <li>{@link Preference#CAT_JAR}</li>
    * <li>{@link Preference#SOURCE_LOCATION_METHOD}</li>
    * <li>{@link Preference#PLE_CONFIGURATION_CACHE_FOLDER}</li>
    * <li>{@link Preference#PLE_CONFIGURATION}</li>
    * </ul>
    */

   public CatParameters() {

      String catJarPreference = Preference.CAT_JAR.get();
      this.catJarPath = Paths.get(catJarPreference);

      this.sourceLocationMethod = Preference.SOURCE_LOCATION_METHOD.get();

      String pleConfigurationCacheFolderPreference = Preference.PLE_CONFIGURATION_CACHE_FOLDER.get();
      String pleConfigurationPreference = Preference.PLE_CONFIGURATION.get();

      this.pleConfigurationPath = Paths.get(pleConfigurationCacheFolderPreference, pleConfigurationPreference);
   }

   /**
    * Gets the path to the CAT annotation processor Jar file.
    * 
    * @return path to the CAT Jar file.
    */

   public Path getCatJarPath() {
      return this.catJarPath;
   }

   /**
    * Gets the path to the PLE Configuration file to be used by the CAT annotation processor.
    * 
    * @return the PLE Configuration file path.
    */

   public Path getPleConfigurationPath() {
      return this.pleConfigurationPath;
   }

   /**
    * Get the name of the CAT annotation processor method used locate source files.
    * 
    * @return the source location method name.
    */

   public String getSourceLocationMethod() {
      return this.sourceLocationMethod;
   }

}

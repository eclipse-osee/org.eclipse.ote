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

import javax.annotation.processing.Processor;
import org.eclipse.core.resources.IProject;
import org.eclipse.ote.cat.plugin.preferencepage.Preference;
import org.eclipse.ote.cat.plugin.project.CatNature;
import org.eclipse.ote.cat.plugin.project.CatProjectManager;

/**
 * This class contains "configuration as code" constants for the CAT Plug-In.
 * 
 * @author Loren K. Ashley
 * @implNote The preference store names of the plug-in preferences are also used as JSON object names in the default
 * preferences file. They are defined as constants so that they may be used as {@link com.fasterxml.jackson.annotation}
 * values. For general access, the method {@link Preference#getPreferenceStoreName} should be used to obtain the
 * preference store names.
 */

public class Constants {

   /**
    * When deconfiguring a project for the {@link CatNature} annotation processor Jar file names are compared to this
    * string in a case insensitive manner to identify a CAT annotation processor Jar file. This is done because the
    * {@link Preference#CAT_JAR} may no longer point to the annotation Jar file that needs to be removed.
    */

   public static final String catJarDetectionName = "cat.jar";

   /**
    * The preference store name used to save the location of the CAT annotation processor Jar file.
    */

   public static final String catJarPreferenceStoreName = "CAT_JAR_PATH";

   /**
    * The name of the CAT Plug-In's state file. This file is used to persist the {@link CatProjectManager} cache.
    */

   public static final String catPluginStateFile = "state.dat";

   /**
    * A description string for the CAT Plug-In state file.
    */

   public static final String catPluginStateFileDescription = "CAT Plug-In State File";

   /**
    * The CAT annotation processor identifier. This needs to be set to the {@link Processor} implementation class name
    * of the CAT annotation processor. This should also match the contents of the
    * "META-INF/services/javax.annotation.processing.Processor" file in the CAT annotation processor Jar file.
    */

   public static final String catProcessorIdentifier = "org.eclipse.ote.cat.CatProcessor";

   /**
    * The name of the file used to save the CAT project settings for an Eclipse {@link IProject} that has the
    * {@link CatNature} applied.
    */

   public static final String catProjectInfoFileName = ".catproject";

   /**
    * A description string for CAT project settings files.
    */

   public static final String catProjectInfoFileDescription = "CAT Project Info File";

   /**
    * The preference store name used to save the list of Java Test Script projects that are configured for the CAT
    * annotation processor.
    */

   public static final String jtsProjectsPreferenceStoreName = "JTS_PROJECTS";

   /**
    * The OSGi extension point identifier for project natures.
    */

   public static final String naturesExtensionPointIdentifier = "org.eclipse.core.resources.natures";

   /**
    * The preference store name used to save the path to the folder used to cache PLE Configurations downloaded from the
    * OPLE server.
    */

   public static final String pleConfigurationCacheFolderPreferenceStoreName = "PLE_CONFIGURATION_CACHE_FOLDER";

   /**
    * The preference store name used to save the URL of the OPLE server.
    */

   public static final String pleConfigurationLoaderPreferenceStoreName = "OPLE_SERVER";

   /**
    * The CAT annotation processor command option for the PLE Configuration file path:
    * <ul>
    * <li>{@value #pleConfigurationPathCatOption}</li>
    * </ul>
    */

   public static final String pleConfigurationPathCatOption = "org.eclipse.ote.cat.pleconfigurationpath";

   /**
    * The preference store name used to save the PLE Configuration to be used by the CAT annotation processor.
    */

   public static final String pleConfigurationPreferenceStoreName = "PLE_CONFIGURATION";

   /**
    * The name of the {@value #preferencesInitializerConfigurationElement} configuration element of the
    * {@value #preferencesExtensionPointIdentifier} extension point that specifies the command line option name for the
    * default preferences file.
    */

   public static final String preferencesCommandLineOptionAttributeName = "option";

   /**
    * The OSGi extension point identifier for preference initializers.
    */

   public static final String preferencesExtensionPointIdentifier = "org.eclipse.core.runtime.preferences";

   /**
    * The name of the configuration element of the {@value #preferencesExtensionPointIdentifier} extension point that
    * contains the command line option name for the default preferences file.
    */

   public static final String preferencesInitializerConfigurationElement = "initializer";

   /**
    * The CAT annotation processor command option for the method to location source files:
    * <ul>
    * <li>{@value #sourceLocationMethodCatOption}</li>
    * </ul>
    */

   public static final String sourceLocationMethodCatOption = "org.eclipse.ote.cat.sourcelocationmethod";

   /**
    * The preference store name used to save the source location method for the CAT annotation processor.
    */

   public static final String sourceLocationMethodPreferenceStoreName = "SOURCE_LOCATION_METHOD";

   /**
    * Constructor is private to prevent instantiation of the class.
    */

   private Constants() {
   }

}

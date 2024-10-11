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

/**
 * This package private class contains constants used within the package.
 * 
 * @author Loren K. Ashley
 * @implNote The preference store names of the plug-in preferences are also used as object names in the default
 * preference value JSON file. They are defined as constants so that they may be used as
 * {@link com.fasterxml.jackson.annotation} values. For general access, the method
 * {@link Preference#getPreferenceStoreName} should be used to obtain the preference store names.
 */

class Constants {

   static final String catJarPreferenceStoreName = "CAT_JAR_PATH";
   static final String sourceLocationMethodPreferenceStoreName = "SOURCE_LOCATION_METHOD";
   static final String jtsProjectsPreferenceStoreName = "JTS_PROJECTS";
   static final String pleConfigurationPreferenceStoreName = "PLE_CONFIGURATION";
   static final String pleConfigurationCacheFolderPreferenceStoreName = "PLE_CONFIGURATION_CACHE_FOLDER";
   static final String pleConfigurationLoaderPreferenceStoreName = "OPLE_SERVER";

}

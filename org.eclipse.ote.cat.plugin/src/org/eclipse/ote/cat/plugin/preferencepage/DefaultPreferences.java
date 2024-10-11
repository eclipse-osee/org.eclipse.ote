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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A POJO for deserialization of default preference values from a JSON file.
 * 
 * @author Loren K. Ashley
 */

public class DefaultPreferences {

   @JsonProperty(Constants.catJarPreferenceStoreName)
   private String catJar;

   @JsonProperty(Constants.jtsProjectsPreferenceStoreName)
   private String[] jtsProjects;

   @JsonProperty(Constants.pleConfigurationPreferenceStoreName)
   private String pleConfiguration;

   @JsonProperty(Constants.pleConfigurationCacheFolderPreferenceStoreName)
   private String pleConfigurationCacheFolder;

   @JsonProperty(Constants.pleConfigurationLoaderPreferenceStoreName)
   private String pleConfigurationLoader;

   @JsonProperty(Constants.sourceLocationMethodPreferenceStoreName)
   private String sourceLocationMethod;

   public DefaultPreferences() {
      this.catJar = null;
      this.sourceLocationMethod = null;
      this.jtsProjects = null;
      this.pleConfiguration = null;
      this.pleConfigurationCacheFolder = null;
      this.pleConfigurationLoader = null;
   }

   public String getCatJar() {
      return this.catJar;
   }

   public String[] getJtsProjects() {
      return this.jtsProjects;
   }

   @JsonIgnore
   public String getJtsProjectsCommaList() {
      return //
      Objects.nonNull(this.jtsProjects) //
         ? Arrays.stream(this.jtsProjects).collect(Collectors.joining(",")) //
         : null;
   }

   public String getPleConfiguration() {
      return this.pleConfiguration;
   }

   public String getPleConfigurationCacheFolder() {
      return this.pleConfigurationCacheFolder;
   }

   public String getPleConfigurationLoader() {
      return this.pleConfigurationLoader;
   }

   public String getSourceLocationMethod() {
      return this.sourceLocationMethod;
   }

   public void setCatJar(String catJar) {
      this.catJar = catJar;
   }

   public void setJtsProjects(String[] jtsProjects) {
      this.jtsProjects = jtsProjects;
   }

   public void setPleConfiguration(String pleConfiguration) {
      this.pleConfiguration = pleConfiguration;
   }

   public void setPleConfigurationCacheFolder(String pleConfigurationCacheFolder) {
      this.pleConfigurationCacheFolder = pleConfigurationCacheFolder;
   }

   public void setPleConfigurationLoader(String pleConfigurationLoader) {
      this.pleConfigurationLoader = pleConfigurationLoader;
   }

   public void setSourceLocationMethod(String sourceLocationMethod) {
      this.sourceLocationMethod = sourceLocationMethod;
   }

}

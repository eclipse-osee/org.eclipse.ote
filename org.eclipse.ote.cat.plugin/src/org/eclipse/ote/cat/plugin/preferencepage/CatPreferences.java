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
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.ote.cat.plugin.Constants;
import org.eclipse.ote.cat.plugin.exception.CatErrorCode;
import org.eclipse.ote.cat.plugin.exception.CatPluginException;
import org.eclipse.ote.cat.plugin.util.JsonFileOperations;

/**
 * An object used for deserialization of preference values from a JSON file.
 * 
 * @author Loren K. Ashley
 */

public class CatPreferences {

   /**
    * Save a single instance of a {@link JsonFileOperations} object specialized for reading and writing
    * {@link CatPreferences} objects to and from JSON files.
    */

   private static final JsonFileOperations<CatPreferences> jsonFileOperations =
      new JsonFileOperations<>(CatPreferences.class, "CAT Preferences File");

   /**
    * Reads a JSON file containing preferences for the CAT Plug-In.
    * 
    * @param osPathString the OS path string to the file to be read.
    * @return {@link CatPreferences} object containing the CAT Plug-In preferences read from the file.
    * @throws CatPluginException when an error occurs accessing, reading, or parsing the file.
    */

   public static CatPreferences read(String osPathString) {

      try {
         Path path = Paths.get(osPathString);
         File file = path.toFile();
         return CatPreferences.jsonFileOperations.read(file);
      } catch (Exception e) {
         //@formatter:off
         CatPluginException failedToReadPreferencesFileException =
            new CatPluginException
                   (
                      CatErrorCode.PreferenceFileError,
                        "Failed to read preferences file."     + "\n"
                      + "   Preferences File: " + osPathString + "\n",
                      e
                   );
         //@formatter:on
         throw failedToReadPreferencesFileException;
      }
   }

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

   /**
    * Creates a new {@link CatPreferences} object with all <code>null</code> values.
    */

   public CatPreferences() {
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

   @JsonIgnore
   public void setJtsProjectsCommaList(String jtsProjectsCommaList) {
      this.jtsProjects = jtsProjectsCommaList.split(",");
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

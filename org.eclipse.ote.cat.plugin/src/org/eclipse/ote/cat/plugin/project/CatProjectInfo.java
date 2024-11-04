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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.nio.file.Path;
import java.util.Objects;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ote.cat.plugin.Constants;
import org.eclipse.ote.cat.plugin.exception.CatErrorCode;
import org.eclipse.ote.cat.plugin.exception.CatPluginException;
import org.eclipse.ote.cat.plugin.util.JsonFileOperations;

/**
 * Encapsulates CAT annotation processor parameters for a {@link CatProject}. This class is marshaled to and from the
 * {@value Constants#catProjectInfoFile} file for a CAT project to persist the CAT annotation processor parameters. The
 * {@link CatProjectInfo} class implements the {@link AutoCloseable} interface. Once closed a {@link CatProjectInfo}
 * object becomes immutable and any invocations of setter methods will cause an exception to be thrown.
 * 
 * @author Loren K. Ashley
 */

public class CatProjectInfo implements AutoCloseable {

   /**
    * Saves a single instance of a {@link JsonFileOperations} object specialized for reading and writing
    * {@link CatProjectInfo} objects to and from JSON files.
    */

   //@formatter:off
   private static JsonFileOperations<CatProjectInfo> jsonFileOperations = 
      new JsonFileOperations<>
             (
                CatProjectInfo.class,
                Constants.catProjectInfoFileDescription
             );
   //@formatter:on

   /**
    * Exception thrown when an attempt is made to modify a {@link CatProjectInfo} object after it has been closed.
    */

   //@formatter:off
   private static CatPluginException lockedException =
      new CatPluginException
             (
                CatErrorCode.InternalError,
                "Attempt to modify a locked (immutable) CatProjectInfo object." + "\n"
             );
   //@formatter:on

   /**
    * Predicate used to compare preference values when one or both values might be <code>null</code>. Two
    * <code>null</code> values are considered equal. Non-<code>null</code> values are compared according to the equals
    * method for the type <code>T</code>.
    * 
    * @param <T> the type of values being compared.
    * @param a value to compare.
    * @param b value to compare.
    * @return <code>true</code> when the values are equal; otherwise, <code>false</code>.
    */

   private static <T> boolean equals(T a, T b) {

      if (Objects.nonNull(a) ^ Objects.nonNull(b)) {
         return false;
      }

      if (Objects.nonNull(a) && !a.equals(b)) {
         return false;
      }

      return true;
   }

   /**
    * Reads the {@value Constants#catProjectInfoFileName} file for the <code>project</code>.
    * 
    * @param project the {@link IProject} used to locate the {@value Constants#catProjectInfoFileName} file.
    * @return a {@link CatProjectInfo} object with the preference values read from the file.
    * @throws CatPluginException when unable to read the file.
    */

   public static CatProjectInfo read(IProject project) {
      try {
         IFile iFile = project.getFile(Constants.catProjectInfoFileName);
         try (CatProjectInfo catProjectInfo = CatProjectInfo.jsonFileOperations.read(iFile)) {
            return catProjectInfo;
         }
      } catch (Exception e) {
         //@formatter:off
         CatPluginException readCatProjectInfoException =
            new CatPluginException
                   (
                      CatErrorCode.CatProjectInfoFileError,
                        "Failed to read \"" + Constants.catProjectInfoFileName + "\" file." + "\n"
                      + "   Project: " + project.toString()                                 + "\n",
                      e
                   );
         //@formatter:on
         throw readCatProjectInfoException;
      }
   }

   /**
    * Writes the <code>catProjectInfo</code> to the {@value Constants#catProjectInfoFileName} file in the
    * <code>project</code>.
    * 
    * @param project the {@link IProject} to write the {@value Constants#catProjectInfoFileName} file in.
    * @param catProjectInfo the CAT Plug-In preferences to be saved.
    * @throws CatPluginException when unable to write the file.
    */

   public static void write(IProject project, CatProjectInfo catProjectInfo) {
      try {
         IFile iFile = project.getFile(Constants.catProjectInfoFileName);
         CatProjectInfo.jsonFileOperations.write(iFile, catProjectInfo);
      } catch (Exception e) {
         //@formatter:off
         CatPluginException writeCatProjectInfoException =
            new CatPluginException
                   (
                      CatErrorCode.CatProjectInfoFileError,
                        "Failed to write \"" + Constants.catProjectInfoFileName + "\" file." + "\n"
                      + "   Project: " + project.toString()                                  + "\n",
                      e
                   );
         //@formatter:on
         throw writeCatProjectInfoException;
      }
   }

   /**
    * Saves the path to the CAT annotation processor Jar file.
    */

   private Path catJarPath;

   /**
    * Lock flag used to make the {@link CatProjectInfo} object immutable by causing the setter methods to throw
    * exceptions instead of modifying values.
    */

   @JsonIgnore
   private boolean locked;

   /**
    * Save the path to the PLE Configuration file for the CAT annotation processor.
    */

   private Path pleConfigurationPath;

   /**
    * Saves the name of the project the contained preferences are for.
    */

   private String projectName;

   /**
    * Saves the Source Location Method to be used by the CAT annotation processor.
    */

   private String sourceLocationMethod;

   /**
    * Creates a new mutable (open) {@link CatProjectInfo} object with <code>null</code> values.
    */

   public CatProjectInfo() {
      this.projectName = null;
      this.catJarPath = null;
      this.sourceLocationMethod = null;
      this.pleConfigurationPath = null;
      this.locked = false;
   }

   /**
    * Creates a new immutable (closed) {@link CatProjectInfo} object with the CAT Plug-In preference values from the
    * <code>catParameters</code>.
    * 
    * @param project the {@link IProject} the {@link CatProjectInfo} object is being created for.
    * @param catParameters a snapshot of the CAT Plug-In preference values.
    */

   public CatProjectInfo(IProject project, CatParameters catParameters) {
      this.projectName = project.toString();
      this.catJarPath = catParameters.getCatJarPath();
      this.sourceLocationMethod = catParameters.getSourceLocationMethod();
      this.pleConfigurationPath = catParameters.getPleConfigurationPath();
      this.locked = true;
   }

   /**
    * Throws a the {@link CatProjectInfo#lockedException} when this {@link CatProjectInfo} object has been locked
    * (closed).
    */

   private void checkLock() {
      if (this.locked) {
         throw CatProjectInfo.lockedException;
      }
   }

   /**
    * Compares this {@link CatProjectInfo} object with another for equality.
    * 
    * @param otherCatProjectInfo the {@link CatProjectInfo} to be compared with.
    * @return <code>true</code> when all members of both objects are equal; otherwise, <code>false</code>.
    */

   @JsonIgnore
   public boolean equals(CatProjectInfo otherCatProjectInfo) {

      //@formatter:off
      return
         Objects.nonNull( otherCatProjectInfo )
      && CatProjectInfo.equals( this.projectName,          otherCatProjectInfo.projectName          )
      && CatProjectInfo.equals( this.catJarPath,           otherCatProjectInfo.catJarPath           )
      && CatProjectInfo.equals( this.sourceLocationMethod, otherCatProjectInfo.sourceLocationMethod )
      && CatProjectInfo.equals( this.pleConfigurationPath, otherCatProjectInfo.pleConfigurationPath )
      ; 
      //@formatter:on
   }

   public Path getCatJarPath() {
      return this.catJarPath;
   }

   public Path getPleConfigurationPath() {
      return this.pleConfigurationPath;
   }

   public String getProjectName() {
      return this.projectName;
   }

   public String getSourceLocationMethod() {
      return this.sourceLocationMethod;
   }

   /**
    * Closes this {@link CatProjectInfo} object making it immutable. The setter methods will throw the
    * {@link CatProjectInfo#lockedException} once the {@link CatProjectInfo} object has been closed.
    */

   @JsonIgnore
   public void close() {
      this.locked = true;
   }

   public void setCatJarPath(Path catJarPath) {
      this.checkLock();
      this.catJarPath = catJarPath;
   }

   public void setPleConfigurationPath(Path pleConfigurationPath) {
      this.checkLock();
      this.pleConfigurationPath = pleConfigurationPath;
   }

   public void setProjectName(String projectName) {
      this.checkLock();
      this.projectName = projectName;
   }

   public void setSourceLocationMethod(String sourceLocationMethod) {
      this.checkLock();
      this.sourceLocationMethod = sourceLocationMethod;
   }

}

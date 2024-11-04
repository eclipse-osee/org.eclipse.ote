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

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import org.eclipse.core.resources.IProject;
import org.eclipse.ote.cat.plugin.Constants;
import org.eclipse.ote.cat.plugin.exception.CatErrorCode;
import org.eclipse.ote.cat.plugin.exception.CatPluginException;
import org.eclipse.ote.cat.plugin.util.JsonFileOperations;

/**
 * Encapsulates an array of CAT annotation processor parameters for the projects in the workspace with the CAT nature.
 * This class is marshaled to and from the {@value Constants#catPluginStateFile} plug-in state file.
 * 
 * @author Loren K. Ashley
 */

public class CatProjectsInfo {

   /**
    * Saves a single instance of a {@link JsonFileOperations} object specialized for reading and writing
    * {@link CatProjectsInfo} objects to and from JSON files.
    */

   //@formatter:off
   private static final JsonFileOperations<CatProjectsInfo> jsonFileOperations =
      new JsonFileOperations<>
             (
                CatProjectsInfo.class,
                Constants.catPluginStateFileDescription
             );
   //@formatter:on

   /**
    * Reads the <code>file</code> and parses the contents as a JSON array of {@link CatInfoProject} objects. A
    * {@link CatProject} without an {@link IProject} reference is created for each {@link CatInfoProject} read. The
    * {@link CatProject} objects are provided to the <code>adder</code> as they are created.
    * 
    * @param file the file to read from.
    * @param adder a {@link Consumer}
    */

   public static void read(File file, Consumer<CatProject> adder) {

      CatProjectsInfo catProjectsInfo = null;

      try {
         catProjectsInfo = (CatProjectsInfo) CatProjectsInfo.jsonFileOperations.read(file);
      } catch (Exception e) {
         //@formatter:off
         CatPluginException catPluginStateFileWriteException =
            new CatPluginException
                   (
                      CatErrorCode.CatPluginStateFileError,
                        "Failed to read the CAT Plugin state file." + "\n"
                      + "File: " + file.getPath().toString()        + "\n"
                   );
         //@formatter:off
         throw catPluginStateFileWriteException;
      }
      
      //@formatter:off
      Arrays
         .stream( catProjectsInfo.getCatProjectInfo() )
         .filter( ( catProjectInfo ) -> Objects.nonNull( catProjectInfo.getProjectName() ) )
         .map( CatProject::new )
         .forEach( adder );
      //@formatter:on
   }

   /**
    * Writes the <code>catProjectsInfo</code> to the <code>file</code> as a JSON array of {@link CatProjectInfo}
    * objects.
    * 
    * @param file the file to be written to.
    * @param catProjectsInfo the {@link CatProjectInfo} objects to be saved.
    */

   public static void write(File file, CatProjectsInfo catProjectsInfo) {
      try {
         CatProjectsInfo.jsonFileOperations.write(file, catProjectsInfo);
      } catch (Exception e) {
         //@formatter:off
         CatPluginException catPluginStateFileWriteException =
            new CatPluginException
                   (
                      CatErrorCode.CatPluginStateFileError,
                        "Failed to write the CAT Plugin state file." + "\n"
                      + "File: " + file.getPath().toString()         + "\n"
                   );
         //@formatter:off
         throw catPluginStateFileWriteException;
      }
   }

   /**
    * Saves an unordered array of {@link CatProjectInfo} objects.
    */
   
   private CatProjectInfo[] catProjectInfo;

   public CatProjectsInfo() {
      this.catProjectInfo = null;
   }

   public CatProjectsInfo(CatProjectInfo[] catProjectInfo) {
      this.catProjectInfo = catProjectInfo;
   }

   public CatProjectInfo[] getCatProjectInfo() {
      return this.catProjectInfo;
   }

   public void setCatProjectInfo(CatProjectInfo[] catProjectInfo) {
      this.catProjectInfo = catProjectInfo;
   }

}

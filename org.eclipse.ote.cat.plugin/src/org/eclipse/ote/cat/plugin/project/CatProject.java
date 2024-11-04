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
import java.util.Objects;
import java.util.Optional;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.apt.core.util.AptConfig;
import org.eclipse.jdt.apt.core.util.IFactoryPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ote.cat.plugin.CatPlugin;
import org.eclipse.ote.cat.plugin.Constants;
import org.eclipse.ote.cat.plugin.exception.CatErrorCode;
import org.eclipse.ote.cat.plugin.exception.CatPluginException;

/**
 * A CAT project represents a view of a project resource with a Java project nature that is configured for building with
 * the CAT annotation processor. The {@link CatProject} maintains the CAT annotation processor settings so the Java
 * project can be reconfigured or deconfigured.
 * 
 * @author Loren K. Ashley
 * @implNote The {@link CatProject} is implemented as an immutable object. The CAT annotation processor parameters are
 * also contained within an immutable object. When changes are made to the CAT annotation processor settings a new
 * {@link CatProject} is created and the original {@link CatProject} object is replaced in the
 * {@link CatProjectManager}.
 */

public class CatProject {

   /**
    * Save a reference to the Eclipse project resource with the {@link CatNature} this {@link CatProject} represents.
    */

   private final IProject project;

   /**
    * Saves the CAT annotation processor parameters. This member is read from and saved in the
    * {@value Constants#catProjectInfoFileName} file in the project's root directory.
    */

   private final CatProjectInfo catProjectInfo;

   /**
    * Creates a new {@link CatProject} for the <code>project</code> with the CAT annotation processor parameters from
    * the <code>catParameters</code>.
    * 
    * @param project the {@link IProject} resource with a {@link CatNature} to be wrapped.
    * @param catParameters the CAT preferences the CAT annotation processor parameters are derived from.
    */

   public CatProject(IProject project, CatParameters catParameters) {
      this.project = project;
      this.catProjectInfo = new CatProjectInfo(project, catParameters);
   }

   /**
    * Creates a new {@link CatProject} for the <code>project</code> with the CAT annotation processor parameters from
    * the <code>catParameters</code>.
    * 
    * @param project the {@link IProject} resource with a {@link CatNature} to be wrapped.
    * @param catProjectInfo the CAT annotation processor parameters.
    */

   public CatProject(IProject project, CatProjectInfo catProjectInfo) {
      this.project = project;
      this.catProjectInfo = catProjectInfo;
   }

   /**
    * Creates a new {@link CatProject} without a {@link IProject} reference with the CAT annotation processor parameters
    * from <code>catProjectInfo</code>.
    * 
    * @param catProjectInfo the CAT annotation processor parameters.
    * @implNote This constructor is used when creating {@link CatProject}s from the CAT Plug-In state file. A
    * {@link CatProjectManager} background task will associate the {@link CatProject} with an Eclipse {@link IProject}.
    * If the CAT project referenced by the <code>catProjectInfo</code> object is not found, the {@link CatProject} will
    * be removed from the {@link CatProjectManager}.
    */

   public CatProject(CatProjectInfo catProjectInfo) {
      this.project = null;
      this.catProjectInfo = catProjectInfo;
   }

   /**
    * Creates a new {@link CatProject} for the <code>project</code> with the {@link CatProjectInfo} CAT annotation
    * processor parameters read from the <code>project</code>'s {@value Constants#catProjectInfoFileName} file.
    * 
    * @param project the Eclipse {@link IProject} resource to be wrapped.
    * @return a new {@link CatProject} wrapping the <code>project</code> with the CAT annotation parameters read from
    * the {@value Constants#catProjectInfoFileName}.
    */

   public static CatProject create(IProject project) {
      CatProjectInfo catProjectInfo = CatProjectInfo.read(project);
      return new CatProject(project, catProjectInfo);
   }

   /**
    * &quot;Updates&quot; the <code>catProject</code> with the {@link CatProjectInfo} read from the file
    * {@value Constants#catProjectInfoFileName} by creating a new {@link CatProject} when the read
    * {@link CatProjectInfo} is different than the {@link CatProjectInfo} in the <code>catProject</code>.
    * 
    * @param catProject the {@link CatProject} to update.
    * @param project the {@link IProject} expected to be wrapped by the {@link CatProject}.
    * @return an {@link Optional} containing the updated {@link CatProject} when the read {@link CatProjectInfo} is
    * different; otherwise, an empty {@link Optional}.
    */

   public static Optional<CatProject> updateCatProject(CatProject catProject, IProject project) {

      CatProjectInfo catProjectInfo = CatProjectInfo.read(project);
      //@formatter:off
      return
            Objects.nonNull( catProject.catProjectInfo )
         && catProject.catProjectInfo.equals( catProjectInfo )
         && Objects.nonNull( catProject.project )
         && catProject.project.equals( project )
         ? Optional.empty()
         : Optional.of( new CatProject( project, catProjectInfo ) );
      //@formatter:on
   }

   /**
    * Deletes the {@value Constants#catProjectInfoFileName} file when a {@link CatProject} is being deconfigured.
    * 
    * @throws CatPluginException when unable to delete the {@value Constants#catProjectInfoFileName} file.
    */

   private void removeCatProjectInfo() {
      IProject project = this.getProject();
      IFile iFile = project.getFile(Constants.catProjectInfoFileName);
      try {
         iFile.delete(true, null);
      } catch (Exception e) {
         //@formatter:off
         CatPluginException catProjectInfoFileException =
            new CatPluginException
                   (
                      CatErrorCode.CatProjectInfoFileError,
                        "Unable to delete the \"" + Constants.catProjectInfoFileName + "\" file." + "\n"
                      + "   Project: " + this.project.toString()                                  + "\n",
                      e
                   );
         //@formatter:on
         throw catProjectInfoFileException;
      }
   }

   /**
    * Gets the CAT annotation processor parameters for the {@link CatProject}.
    * 
    * @return the CAT annotation processor parameters.
    */

   public CatProjectInfo getCatProjectInfo() {
      return this.catProjectInfo;
   }

   /**
    * Gets the Eclipse {@link IProject} resource wrapped by the {@link CatProject}.
    * 
    * @return the {@link IProject} for the {@link CatProject}.
    */

   public IProject getProject() {
      return this.project;
   }

   /**
    * Updates the CAT annotation processor Jar file path for the {@link CatProject}. When <code>newCatJarPath</code> and
    * <code>oldCatJarPath</code> are the same no changes to the project are made.
    * 
    * @param catProjectInfo when <code>newCatJarPath</code> is non-<code>null</code> it is set in the
    * {@link CatProjectInfo} object.
    * @param newCatJarPath the new Jar file path. Set this parameter to <code>null</code> when the Jar file path is to
    * be removed from the Java project's {@link IFactoryPath}.
    * @param oldCatJarPath the original Jar file path. Set this parameter to <code>null</code> when configuring a
    * project for the first time.
    * @throws CatPluginException when unable to update the {@link IJavaProject}'s {@link IFactoryPath}.
    */

   private void updateFactoryPath(CatProjectInfo catProjectInfo, Path newCatJarPath, Path oldCatJarPath) {

      try {

         if (Objects.nonNull(newCatJarPath)) {

            catProjectInfo.setCatJarPath(newCatJarPath);

            if (newCatJarPath.equals(oldCatJarPath)) {
               return;
            }
         }

         IJavaProject javaProject = JavaCore.create(this.project);

         boolean factoryPathChanged = false;
         IFactoryPath factoryPath = AptConfig.getFactoryPath(javaProject);

         if (Objects.nonNull(oldCatJarPath)) {
            factoryPath.removeExternalJar(oldCatJarPath.toFile());
            factoryPathChanged = true;
         }

         if (Objects.nonNull(newCatJarPath)) {
            factoryPath.addExternalJar(newCatJarPath.toFile());
            factoryPathChanged = true;
         }

         if (factoryPathChanged) {
            AptConfig.setFactoryPath(javaProject, factoryPath);
         }

      } catch (Exception e) {
         //@formatter:off
         CatPluginException updateFactoryPathException =
            new CatPluginException
                   (
                      CatErrorCode.InternalError,
                        "Failed to update the underlying Java project's Factory Path." + "\n"
                      + "   Project:          " + this.project.toString()              + "\n"
                      + "   New CAT Jar Path: " + newCatJarPath                        + "\n"
                      + "   Old CAT Jar Path: " + oldCatJarPath                        + "\n",
                      e
                   );
         //@formatter:on
         throw updateFactoryPathException;
      }
   }

   /**
    * Updates the CAT annotation processor Source Location Method option parameter the {@link CatProject}. When
    * <code>newSourceLocationMethod</code> and <code>oldSourceLocationMethod</code> are the same no changes to the
    * project are made.
    * 
    * @param catProjectInfo when <code>newSourceLocationMethod</code> is non-<code>null</code> it is set in the
    * {@link CatProjectInfo} object.
    * @param newSourceLocationMethod the new Source Location Method. Set this parameter to <code>null</code> when CAT
    * annotation processor option is to be removed from the Java project's annotation processor options.
    * @param oldSourceLocationMethod the original Source Location Method. Set this parameter to <code>null</code> when
    * configuring a project for the first time.
    * @throws CatPluginException when unable to update the {@link IJavaProject}'s annotation processor options.
    */

   private void updateSourceLocationMethod(CatProjectInfo catProjectInfo, String newSourceLocationMethod, String oldSourceLocationMethod) {

      try {

         if (Objects.nonNull(newSourceLocationMethod)) {

            catProjectInfo.setSourceLocationMethod(oldSourceLocationMethod);

            if (newSourceLocationMethod.equals(oldSourceLocationMethod)) {
               return;
            }
         }

         IJavaProject javaProject = JavaCore.create(this.project);

         if (Objects.nonNull(oldSourceLocationMethod)) {
            AptConfig.removeProcessorOption(javaProject, CatParameters.getSourceLocationMethodKey());
         }

         if (Objects.nonNull(newSourceLocationMethod)) {
            AptConfig.addProcessorOption(javaProject, CatParameters.getSourceLocationMethodKey(),
               newSourceLocationMethod);
         }

      } catch (Exception e) {
         //@formatter:off
         CatPluginException updateSourceLocationMethodException =
            new CatPluginException
                   (
                      CatErrorCode.InternalError,
                        "Failed to update the underlying Java project's annotation processor options" + "\n"
                      + "   Project:                    " + this.project.toString()                   + "\n"
                      + "   New Source Location Method: " + newSourceLocationMethod                   + "\n"
                      + "   Old Source Lcoation Method: " + oldSourceLocationMethod                   + "\n",
                      e
                   );
         //@formatter:on
         throw updateSourceLocationMethodException;
      }
   }

   /**
    * Updates the CAT annotation processor PLE Configuration Path option parameter the {@link CatProject}. When
    * <code>newPleConfigurationPath</code> and <code>oldPleConfigurationPath</code> are the same no changes to the
    * project are made.
    * 
    * @param catProjectInfo when <code>newPleConfigurationPath</code> is non-<code>null</code> it is set in the
    * {@link CatProjectInfo} object.
    * @param newPleConfigurationPath the new PLE Configuration . Set this parameter to <code>null</code> when CAT
    * annotation processor option is to be removed from the Java project's annotation processor options.
    * @param oldPleConfigurationPath the original Source Location Method. Set this parameter to <code>null</code> when
    * configuring a project for the first time.
    * @throws CatPluginException when unable to update the {@link IJavaProject}'s annotation processor options.
    */

   private void updatePleConfigurationPath(CatProjectInfo catProjectInfo, Path newPleConfigurationPath, Path oldPleConfigurationPath) {

      try {

         if (Objects.nonNull(newPleConfigurationPath)) {

            catProjectInfo.setPleConfigurationPath(oldPleConfigurationPath);

            if (newPleConfigurationPath.equals(oldPleConfigurationPath)) {
               return;
            }
         }

         IJavaProject javaProject = JavaCore.create(this.project);

         if (Objects.nonNull(oldPleConfigurationPath)) {
            AptConfig.removeProcessorOption(javaProject, CatParameters.getPleConfigurationPathKey());
         }

         if (Objects.nonNull(newPleConfigurationPath)) {
            AptConfig.addProcessorOption(javaProject, CatParameters.getPleConfigurationPathKey(),
               newPleConfigurationPath.toString());
         }

      } catch (Exception e) {
         //@formatter:off
         CatPluginException updatePleConfigurationPathException =
            new CatPluginException
                   (
                      CatErrorCode.InternalError,
                        "Failed to update the underlying Java project's annotation processor options" + "\n"
                      + "   Project:                    " + this.project.toString()                   + "\n"
                      + "   New PLE Configuration Path: " + newPleConfigurationPath                   + "\n"
                      + "   Old PLE Configuration Path: " + oldPleConfigurationPath                   + "\n",
                      e
                   );
         //@formatter:on
         throw updatePleConfigurationPathException;
      }
   }

   /**
    * Updates the {@link CatProject}'s CAT annotation processor parameters from the current values in the CAT Plug-In's
    * preference store. When a change is made to the CAT project's settings:
    * <ul>
    * <li>the new settings are written to the project's {@value Constants#catProjectInfoFileName} file,</li>
    * <li>this {@link CatProject} object is removed from the {@link CatProjectManager}, and</li>
    * <li>a new {@link CatProject} object with the new CAT annotation processor parameters is added to the
    * {@link CatProjectManager}.</li>
    * </ul>
    */

   public void update() {

      CatProject newCatProject = null;

      try (CatProjectInfo newCatProjectInfo = new CatProjectInfo()) {

         newCatProjectInfo.setProjectName(this.project.toString());
         CatParameters catParameters = new CatParameters();

         Path oldCatJarPath = this.catProjectInfo.getCatJarPath();
         Path newCatJarPath = catParameters.getCatJarPath();
         this.updateFactoryPath(newCatProjectInfo, newCatJarPath, oldCatJarPath);

         String oldSourceLocationMethod = this.catProjectInfo.getSourceLocationMethod();
         String newSourceLocationMethod = catParameters.getSourceLocationMethod();
         this.updateSourceLocationMethod(newCatProjectInfo, newSourceLocationMethod, oldSourceLocationMethod);

         Path oldPleConfigurationPath = this.catProjectInfo.getPleConfigurationPath();
         Path newPleConfigurationPath = catParameters.getPleConfigurationPath();
         this.updatePleConfigurationPath(newCatProjectInfo, newPleConfigurationPath, oldPleConfigurationPath);

         if (this.catProjectInfo.equals(newCatProjectInfo)) {
            return;
         }

         CatProjectInfo.write(project, newCatProjectInfo);
         newCatProject = new CatProject(this.project, newCatProjectInfo);
      }

      if (Objects.nonNull(newCatProject)) {
         CatPlugin.getCatProjectManager().addCatProject(newCatProject);
      }

   }

   /**
    * Configures the {@link CatProject}'s CAT annotation processor parameters from the current values in the CAT
    * Plug-In's preference store. The CAT project's settings are saved into the
    * {@value Constants#catProjectInfoFileName} file. This {@link CatProject} is added to the {@link CatProjectManager}.
    */

   public void configure() {

      CatProject newCatProject = null;

      try (CatProjectInfo newCatProjectInfo = new CatProjectInfo()) {

         Path newCatJarPath = this.catProjectInfo.getCatJarPath();
         this.updateFactoryPath(newCatProjectInfo, newCatJarPath, null);

         String newSourceLocationMethod = this.catProjectInfo.getSourceLocationMethod();
         this.updateSourceLocationMethod(newCatProjectInfo, newSourceLocationMethod, null);

         Path newPleConfigurationPath = this.catProjectInfo.getPleConfigurationPath();
         this.updatePleConfigurationPath(newCatProjectInfo, newPleConfigurationPath, null);

         CatProjectInfo.write(project, newCatProjectInfo);
         newCatProject = new CatProject(this.project, newCatProjectInfo);
      }

      if (Objects.nonNull(newCatProject)) {
         CatPlugin.getCatProjectManager().addCatProject(newCatProject);
      }

   }

   /**
    * Removes the {@link CatProject}'s CAT annotation processor parameters from project. The CAT project's settings are
    * saved into the {@value Constants#catProjectInfoFileName} file is deleted and this {@link CatProject} is removed
    * from the {@link CatProjectManager}..
    */

   public void deconfigure() {

      CatProjectInfo newCatProjectInfo = new CatProjectInfo();

      Path oldCatJarPath = this.catProjectInfo.getCatJarPath();
      this.updateFactoryPath(newCatProjectInfo, null, oldCatJarPath);

      String oldSourceLocationMethod = this.catProjectInfo.getSourceLocationMethod();
      this.updateSourceLocationMethod(newCatProjectInfo, null, oldSourceLocationMethod);

      Path oldPleConfigurationPath = this.catProjectInfo.getPleConfigurationPath();
      this.updatePleConfigurationPath(newCatProjectInfo, null, oldPleConfigurationPath);

      this.removeCatProjectInfo();
      CatPlugin.getCatProjectManager().removeCatProject(this.project);

   }

   /**
    * {@inheritDoc}
    */

   public String toString() {
      return this.catProjectInfo.getProjectName();
   }

}

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

import java.util.Arrays;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ote.cat.plugin.CatPlugin;
import org.eclipse.ote.cat.plugin.exception.CatErrorCode;
import org.eclipse.ote.cat.plugin.exception.CatPluginException;
import org.eclipse.ote.cat.plugin.preferencepage.Preference;

/**
 * An implementation of the {@link IProjectNature} interface used to configure and deconfigure projects for building
 * with the CAT annotation processor.
 * 
 * @author Loren K. Ashley
 */

public class CatNature implements IProjectNature {

   /**
    * Saves a reference to the project this {@link CatNature} instance is applied to.
    */

   private IProject project;

   /**
    * The nature configure method is used to apply the Java compiler options needed for the CAT annotation processor.
    * <p>
    * {@inheritDoc}
    */

   @Override
   public void configure() throws CoreException {
      try {
         CatParameters catParameters = new CatParameters();
         CatProject catProject = new CatProject(this.project, catParameters);
         CatProjectManager catProjectManager = CatPlugin.getCatProjectManager();
         catProjectManager.addCatProject(catProject);
         catProject.configure();
      } catch (Exception e) {
         //@formatter:off
         CatPluginException catNatureConfigurationException =
            new CatPluginException
                   (
                      CatErrorCode.InternalError,
                        "Failed to configure project for the CAT annotation processor." + "\n"
                      + "   Project: " + this.project.toString()                        + "\n",
                      e
                   );
         //@formatter:on
         catNatureConfigurationException.log();
      }
   }

   /**
    * The nature deconfigure method is used to remove the Java compiler options for the CAT annotation processor from
    * the project.
    * <p>
    * {@inheritDoc}
    */

   @Override
   public void deconfigure() throws CoreException {
      //@formatter:off
      CatPlugin
         .getCatProjectManager()
         .removeCatProject(this.project)
         .ifPresent( CatProject::deconfigure );

      Preference.JTS_PROJECTS.set
         (
            Arrays
               .stream( Preference.JTS_PROJECTS.get().split( "," ) )
               .filter( ( jtsProject ) -> !jtsProject.equals( this.project.toString() ) )
               .collect( Collectors.joining( "," ) )
         );
      //@formatter:on

   }

   /**
    * {@inheritDoc}
    */

   @Override
   public IProject getProject() {
      return this.project;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void setProject(IProject project) {
      this.project = project;
   }

}

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

package org.eclipse.ote.cat.plugin.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * A class of static utility methods for {@link IProject} operations.
 * 
 * @author Loren K. Ashley
 */

public class Projects {

   /**
    * Adds the project nature specified by <code>natureIdentifier</code> to the start of the <code>project</code>'s
    * nature list. When the <code>project</code> is closed, <code>project</code> is <code>null</code>, or
    * <code>natureIdentifier</code> is <code>null</code> no action is performed.
    * 
    * @param project the <code>project</code> to add the nature to.
    * @param natureIdentifier the identifier of the project nature to add.
    * @throws OseeCoreException when:
    * <ul>
    * <li>unable to obtain the project's list of natures,</li>
    * <li>the modified list of natures fails to validate with {@link IWorkspace#validateNatureSet}, or</li>
    * <li>unable to set the project's list of natures.</li>
    * </ul>
    */

   public static void addNature(IProject project, String natureIdentifier) {
      if (Objects.isNull(project) || !project.isOpen() || Objects.isNull(natureIdentifier)) {
         return;
      }
      IProjectDescription projectDescription = Projects.getProjectDescription(project);
      String[] natures = projectDescription.getNatureIds();
      String[] newNatures = new String[natures.length + 1];
      System.arraycopy(natures, 0, newNatures, 1, natures.length);
      newNatures[0] = natureIdentifier;
      Projects.validateNatureSet(newNatures);
      projectDescription.setNatureIds(newNatures);
      Projects.setProjectDescription(project, projectDescription);
   }

   /**
    * A wrapper on the method {@link IProject}<code>::getDescription</code> that catches any checked exceptions and
    * throws them as a runtime {@link OseeCoreException}.
    * 
    * @param project the {@link IProject} to get the {@link IProjectDescription} from.
    * @return the <code>project</code>'s {@link IProjectDescription}.
    * @throws OseeCoreException when unable to get the <code>projectDescription</code> from the <code>project</code>.
    */

   public static IProjectDescription getProjectDescription(IProject project) {
      try {
         IProjectDescription projectDescription = project.getDescription();
         return projectDescription;
      } catch (Exception e) {
         //@formatter:off
         org.eclipse.osee.framework.jdk.core.type.OseeCoreException getNatureListException =
            new OseeCoreException
                   (
                        "Failed to obtain the \"IProjectDescription\" for a project." + "\n"
                      + "Project: " + project,
                      e
                   );
         //@formatter:on
         throw getNatureListException;
      }
   }

   /**
    * Generates a map of the projects in the workspace.
    * 
    * @param <K> The type of the map key.
    * @param keyExtractor a {@link Function} used to generate a map key for each {@link IProject}.
    * @return a {@link Map} of the {@link IProject}s in the workspace.
    * @throws OseeCoreException when unable to obtain the projects from the workspace.
    */

   public static <K> Map<K, IProject> getProjectMap(Function<IProject, K> keyExtractor) {
      Map<K, IProject> map = new HashMap<>();
      IProject[] projects = Projects.getProjects();
      for (int i = 0; i < projects.length; i++) {
         IProject project = projects[i];
         K key = keyExtractor.apply(projects[i]);
         map.put(key, project);
      }
      return map;
   }

   /**
    * Generates a pair of project maps from the workspace. One map contains all of the open projects with the specified
    * project nature and the other map contains all of the open projects without the project nature.
    * 
    * @param <K> The type of the map key.
    * @param natureIdentifier the identifier of the project nature to separate the workspace projects with.
    * @param keyExtractor a {@link Function} used to generate a map key for each {@link IProject}.
    * @return a {@link Pair} with the first element being a {@link Map} of the open {@link IProject}s with the nature
    * specified by <code>natureIdentifier</code> and the second element being a {@link Map} of the open
    * {@link IProject}s without the nature.
    * @throws OseeCoreException when unable to obtain the projects from the workspace.
    */

   public static <C> Pair<C, C> getProjectsForNature(String natureIdentifier, Supplier<C> containerFactory, BiConsumer<C, IProject> containerAdder) {
      C projectsWithNature = containerFactory.get();
      C projectsWithoutNature = containerFactory.get();
      Pair<C, C> pair = new Pair<>(projectsWithNature, projectsWithoutNature);
      IProject[] projectArray = Projects.getProjects();
      for (IProject project : projectArray) {
         if (!project.isOpen()) {
            continue;
         }
         if (Projects.hasNature(project, natureIdentifier)) {
            containerAdder.accept(projectsWithNature, project);
         } else {
            containerAdder.accept(projectsWithoutNature, project);
         }
      }
      return pair;
   }

   /**
    * Gets an array of the {@link IProject}s in the workspace.
    * 
    * @return an array of the {@link IProject}s from the workspace.
    * @throws OseeCoreException when unable to obtain the workspace projects.
    */

   public static IProject[] getProjects() {
      try {
         IWorkspace workspace = ResourcesPlugin.getWorkspace();
         IWorkspaceRoot workSpaceRoot = workspace.getRoot();
         IProject[] projectArray = workSpaceRoot.getProjects();
         return projectArray;
      } catch (Exception e) {
         //@formatter:off
         OseeCoreException getProjectsException =
            new OseeCoreException
                   (
                      "Failed to get projects from the workspace."
                   );
         //@formatter:on
         throw getProjectsException;
      }
   }

   /**
    * Gets a list of open projects in the workspace with the nature specified by <code>natureIdentifier</code>.
    * 
    * @param natureIdentifier only projects that have a nature with this identifier are added to the list.
    * @return a {@link LinkedList} of the workspace {@link IProject}s with the nature specified by
    * <code>natureIdentifier</code>. When no projects are found with the nature, an empty {@link LinkedList} is
    * returned.
    * @throws OseeCoreException when:
    * <ul>
    * <li>Unable to obtain the workspace projects.</li>
    * <li>Unable to determine if a project has a nature.</li>
    * </ul>
    */

   public static LinkedList<IProject> getProjectsWithNature(String natureIdentifier) {
      LinkedList<IProject> projectsWithNature = new LinkedList<>();
      IProject[] projectArray = Projects.getProjects();
      for (IProject project : projectArray) {
         if (Projects.hasNature(project, natureIdentifier)) {
            projectsWithNature.add(project);
         }
      }
      return projectsWithNature;
   }

   /**
    * Gets a list of open projects in the workspace that do not have the nature specified by
    * <code>natureIdentifier</code>.
    * 
    * @param natureIdentifier only projects that do not have a nature with this identifier are added to the list.
    * @return a {@link LinkedList} of the workspace {@link IProject}s that do not have the nature specified by
    * <code>natureIdentifier</code>. When no projects are found without the nature, an empty {@link LinkedList} is
    * returned.
    * @throws OseeCoreException when:
    * <ul>
    * <li>Unable to obtain the workspace projects.</li>
    * <li>Unable to determine if a project has a nature.</li>
    * </ul>
    */

   public static LinkedList<IProject> getProjectsWithoutNature(String natureIdentifier) {
      LinkedList<IProject> projectsWithoutNature = new LinkedList<>();
      IProject[] projectArray = Projects.getProjects();
      for (IProject project : projectArray) {
         if (!Projects.hasNature(project, natureIdentifier)) {
            projectsWithoutNature.add(project);
         }
      }
      return projectsWithoutNature;
   }

   /**
    * A {@link Predicate} to determine if a project has a nature.
    * 
    * @param project the {@link IProject} to be tested.
    * @param natureIdentifier the identifier of the nature to test for.
    * @return <code>true</code> when the {@link IProject} has the nature specified by <code>natureIdentifier</code>;
    * otherwise, <code>false</code>.
    */

   public static boolean hasNature(IProject project, String natureIdentifier) {
      try {
         return project.isOpen() && project.hasNature(natureIdentifier);
      } catch (Exception e) {
         //@formatter:off
         OseeCoreException cannotDetermineNatureException =
            new OseeCoreException
                   (
                        "Failed to determine if project has the specified nature." + "\n"
                      + "   Project: " + project                                   + "\n"
                      + "   Nature:  " + natureIdentifier                          + "\n"
                   );
         //@formatter:on
         throw cannotDetermineNatureException;
      }
   }

   /**
    * Moves the project nature at the position <code>naturePosition</code> to the start of the <code>project</code>'s
    * nature list.
    * 
    * @param project the project to modify.
    * @param naturePosition the current list position of the project nature to be moved to the start of the project's
    * nature list.
    * @throws OseeCoreException when:
    * <ul>
    * <li>unable to obtain the project's list of natures,</li>
    * <li>the modified list of natures fails to validate with {@link IWorkspace#validateNatureSet}, or</li>
    * <li>unable to set the project's list of natures.</li>
    * </ul>
    */

   public static void moveNatureToTheFirstPosition(IProject project, int naturePosition) {
      IProjectDescription projectDescription = Projects.getProjectDescription(project);
      String[] natures = projectDescription.getNatureIds();
      String[] newNatures = new String[natures.length + 1];
      System.arraycopy(natures, 0, newNatures, 1, naturePosition);
      System.arraycopy(natures, naturePosition + 1, newNatures, naturePosition + 1, natures.length - naturePosition);
      newNatures[0] = natures[naturePosition];
      Projects.validateNatureSet(newNatures);
      projectDescription.setNatureIds(newNatures);
      Projects.setProjectDescription(project, projectDescription);
   }

   /**
    * Finds the position of the project nature specified with <code>natureIdentifier</code> in the
    * <code>project</code>'s list of natures. When the nature is not in the project's nature list, the
    * <code>project</code> is not open, or <code>project</code> is <code>null</code> -1 is returned.
    * 
    * @param project the {@link IProject} to find the nature position.
    * @param natureIdentifier the identifier of the nature to determine the position of.
    * @return when the <code>project</code>'s nature list contains nature specified by <code>natureIdentifier</code>,
    * the index position of the nature in the nature list; otherwise, -1.
    * @throws OseeCoreException when unable to obtain the project's list of natures.
    */

   public static int position(IProject project, String natureIdentifier) {
      if (Objects.isNull(project) || !project.isOpen()) {
         return -1;
      }
      try {
         IProjectDescription projectDescription = project.getDescription();
         String[] natures = projectDescription.getNatureIds();
         for (int i = 0; i < natures.length; i++) {
            if (natureIdentifier.equals(natures[i])) {
               return i;
            }
         }
         return -1;
      } catch (Exception e) {
         //@formatter:off
         OseeCoreException natureListPositionException =
            new OseeCoreException
                   (
                        "Failed to obtain the natures list for a project." + "\n"
                      + "Project: " + project                              + "\n",
                      e
                   );
         //@formatter:on
         throw natureListPositionException;
      }
   }

   /**
    * Removes the nature specified by <code>natureIdentifier</code> from the <code>project</code>.
    * 
    * @param project the {@link IProject} to remove the specified nature from.
    * @param natureIdentifier the nature to remove from the {@link IProject}.
    * @throws OseeCoreException when:
    * <ul>
    * <li>Unable to get or set the <code>project</code>'s {@link IProjectDescription}.</li>
    * <li>The <code>project</code>'s nature set with the nature removed fails to validate.</li>
    * </ul>
    */

   public static void removeProjectNature(IProject project, String natureIdentifier) {
      int position = Projects.position(project, natureIdentifier);
      if (position < 0) {
         return;
      }
      IProjectDescription projectDescription = Projects.getProjectDescription(project);
      String[] natures = projectDescription.getNatureIds();
      String[] newNatures = new String[natures.length - 1];
      System.arraycopy(natures, 0, newNatures, 0, position);
      System.arraycopy(natures, position + 1, newNatures, position, natures.length - position - 1);
      Projects.validateNatureSet(newNatures);
      projectDescription.setNatureIds(newNatures);
      Projects.setProjectDescription(project, projectDescription);
   }

   /**
    * A wrapper on the method {@link IProject}<code>::setDescription</code> that catches any checked exceptions and
    * throws them as a runtime {@link OseeCoreException}.
    * 
    * @param project the {@link IProject} to set the {@link IProjectDescription} for.
    * @param projectDescription the {@link IProjectDescription} to be applied to the <code>project</code>.
    * @throws OseeCoreException when unable to apply the <code>projectDescription</code> to the <code>project</code>.
    */

   public static void setProjectDescription(IProject project, IProjectDescription projectDescription) {
      try {
         project.setDescription(projectDescription, null);
      } catch (Exception e) {
         //@formatter:off
         OseeCoreException setNatureListPositionException =
            new OseeCoreException
                   (
                        "Failed to set the project description." + "\n"
                      + "Project: " + project                    + "\n",
                      e
                   );
         //@formatter:on
         throw setNatureListPositionException;
      }
   }

   /**
    * Validates that all project natures in the array <code>natureSet</code> are compatible according to the workspace.
    * 
    * @param natureSet the set of project natures to be tested.
    * @throws OseeCoreException when the project natures in <code>natureSet</code> are not compatible.
    */

   public static void validateNatureSet(String[] natureSet) {
      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      IStatus status = workspace.validateNatureSet(natureSet);
      if (status.getCode() != IStatus.OK) {
         //@formatter:off
         OseeCoreException natureListPositionException =
            new OseeCoreException
                   (
                        "Project nature set is not compatabile." + "\n"
                      + "   Project Natures: "                   + "\n"
                      + Arrays.stream( natureSet ).collect( Collectors.joining( "      ", ",\n      ", "\n")),
                      status
                   );
         //@formatter:on
         throw natureListPositionException;
      }
   }

   /**
    * Constructor is private to prevent instantiation of the class.
    */

   private Projects() {
   }

}

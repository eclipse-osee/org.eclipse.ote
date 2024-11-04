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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.ote.cat.plugin.CatPlugin;
import org.eclipse.ote.cat.plugin.Constants;
import org.eclipse.ote.cat.plugin.exception.CatErrorCode;
import org.eclipse.ote.cat.plugin.exception.CatPluginException;
import org.eclipse.ote.cat.plugin.preferencepage.Preference;
import org.eclipse.ote.cat.plugin.util.Projects;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Manages a persistent cache of {@link CatProject}s. A CAT project is an Eclipse workspace project that has the
 * {@link CatNature} applied.
 * <p>
 * <h2>Project Files</h2>
 * <dl style="padding-left:3em">
 * <dt>&quot;.project&quot; file:</dt>
 * <dd>This file in the root directory of a project maintains the list of project natures that have been applied to the
 * project.</dd>
 * <dt>{@value org.eclipse.ote.cat.plugin.Constants#catProjectInfoFileName} file:</dt>
 * <dd>This file in the root directory of a project maintains the CAT project settings. The settings file JSON contents
 * are marshaled into and out of instances of the class {@link CatProjectInfo}.</dd>
 * </dl>
 * <p>
 * <h2>Plug-In State File</h2>
 * <p>
 * The method {@link org.eclipse.core.runtime.Plugin#getStateLocation Plugin::getStateLocation} provides the workspace
 * path to the state folder for the plug-in. The {@link CatProjectInfo} for each {@link CatProject} in the cache is
 * saved to the file {@value org.eclipse.ote.cat.plugin.Constants#catPluginStateFile} as an JSON array of
 * {@link CatProjectInfo} objects.
 * </p>
 * <h2>Preferences</h2>
 * <p>
 * The CAT Plug-In preference {@link Preference#JTS_PROJECTS} contains a list of the projects that are expected to have
 * the {@link CatNature} applied.
 * </p>
 * <h2>Cache Initialization</h2>
 * <p>
 * The following steps are taken on CAT Plug-In startup to resolve potential differences in the three configuration
 * sources.
 * <ul>
 * <li>The cache contents are loaded from the CAT Plug-In state file. A background job is started to complete the
 * following:
 * <ul>
 * <li>Workspace projects that do not have a {@link CatNature} applied are removed from the cache.</li>
 * <li>Workspace projects that have a {@link CatNature} that are not in the cache are added to the cache.</li>
 * <li>Workspace projects that have a {@link CatNature} that are in the cache are updated with the
 * {@link CatProjectInfo} data from the {@value org.eclipse.ote.cat.plugin.Constants#catProjectInfoFileName} file for
 * the project.</li>
 * <li>Projects on the {@link Preference#JTS_PROJECTS} list that are not in the cache have the {@link CatNature} applied
 * using the current preference settings. The newly configured project is added to the cache.</li>
 * <li>The {@link Preference#JTS_PROJECTS} is updated with a list of the projects in the cache.</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * <h2>Preference Update</h2>
 * <p>
 * When the CAT Plug-In preferences are changed the Workspace projects are updated as follows:
 * <ul>
 * <li>Cached projects that are not on the {@link Preference#JTS_PROJECTS} list have the {@link CatNature} removed and
 * are deconfigured for the CAT annotation processor.</li>
 * <li>Cached projects that are on the {@link Preference#JTS_PROJECTS} list are updated with any preference
 * changes.</li>
 * <li>Projects on the {@link Preference#JTS_PROJECTS} list that are not in the cache have the {@link CatNature} applied
 * and are configured for the CAT annotation processor with the current preferences.</li>
 * </ul>
 * </p>
 * 
 * @author Loren K. Ashley
 */

public class CatProjectManager implements ISaveParticipant {

   /**
    * Internal class used to encapsulate the project cache and provide synchronized access.
    */

   private class CatProjectCache {

      /**
       * Cache of {@link CatProject}s by project name. This cache is initially populated from the CAT Plug-In state file
       * before the {@link CatProject}s are reassociated with the Eclipse {@link IProject}s.
       */

      private Map<String, CatProject> byName;

      /**
       * Cache of {@link CatProject}s by the {@link IProject} they wrap.
       */

      private Map<IProject, CatProject> byProject;

      /**
       * Writing the CAT Plug-In state file is synchronized on this object instead of the {@link CatProjectCache}
       * instance so the cache is not locked during a save operation.
       */

      private Object saveSyncObject;

      /**
       * Creates a new empty cache.
       */

      CatProjectCache() {
         this.byName = new HashMap<>();
         this.byProject = new HashMap<>();
         this.saveSyncObject = new Object();
      }

      /**
       * Adds a {@link CatProject} to the cache. When the {@link CatProject} does not contain a {@link IProject}
       * reference the {@link CatProject} is only added to the {@link #byName} cache.
       * 
       * @param catProject the {@link CatProject} to be added.
       */

      synchronized public void add(CatProject catProject) {
         IProject project = catProject.getProject();
         if (Objects.nonNull(project)) {
            this.byName.put(project.toString(), catProject);
            this.byProject.put(project, catProject);
         } else {
            CatProjectInfo catProjectInfo = catProject.getCatProjectInfo();
            String name = catProjectInfo.getProjectName();
            this.byName.put(name, catProject);
         }
      }

      /**
       * Gets the {@link CatProject} for the <code>project</code>.
       * 
       * @param project the {@link IProject} to get the associated {@link CatProject} for.
       * @return an {@link Optional} containing the {@link CatProject} associated with the <code>project</code>;
       * otherwise, an empty {@link Optional}.
       */

      synchronized public Optional<CatProject> get(IProject project) {
         CatProject catProject = this.byProject.get(project);
         if (Objects.isNull(catProject)) {
            catProject = this.byName.get(project.toString());
            if (Objects.nonNull(catProject)) {
               this.byProject.put(project, catProject);
            }
         }
         return Optional.ofNullable(catProject);
      }

      /**
       * A list of the names of the projects in the cache. The returned list is not backed by the cache.
       * 
       * @return a {@link List} of the names of the {@link CatProject}s in the cache.
       */

      synchronized List<String> getNames() {
         List<String> list = new LinkedList<>();
         this.byName.keySet().forEach(list::add);
         return list;
      }

      /**
       * Removes the {@link CatProject} associated with the <code>project</code> from the cache.
       * 
       * @param project the {@link IProject} to remove it's associated {@link CatProject}.
       * @return an {@link Optional} containing the removed {@link CatProject} when the cache contained an association;
       * otherwise, an empty {@link Optional}.
       */

      synchronized public Optional<CatProject> remove(IProject project) {
         String name = project.toString();
         CatProject catProjectByName = this.byName.remove(name);
         CatProject catProjectByProject = this.byProject.remove(project);
         if (Objects.nonNull(catProjectByName)) {
            return Optional.of(catProjectByName);
         }
         return Optional.ofNullable(catProjectByProject);
      }

      /**
       * Extracts the {@link CatProjectInfo} objects from the {@link CatProject}s in the cache and saves then in the CAT
       * Plug-In state file. The cache is locked while the {@link CatProjectInfo} objects are being extracted to an
       * array. Once the extraction is complete the cache lock is released. The file write is synchronized on the
       * {@link #saveSyncObject} to prevent more than one thread from attempting to write the file.
       */

      void save() {

         CatProjectInfo[] catProjectInfoArray;

         synchronized (this) {
         //@formatter:off
         catProjectInfoArray =
            this.byName
               .values()
               .stream()
               .map( CatProject::getCatProjectInfo )
               .collect( Collectors.toCollection( ArrayList::new ) )
               .toArray( new CatProjectInfo[this.byName.size()] )
               ;
         //@formatter:on
         }

         synchronized (saveSyncObject) {
            File stateLocationFile = CatPlugin.getStateLocationFile();
            CatProjectsInfo catProjectsInfo = new CatProjectsInfo(catProjectInfoArray);
            CatProjectsInfo.write(stateLocationFile, catProjectsInfo);
         }
      }

   }

   /**
    * When the <code>optional</code> contains a value the <code>presentAction</code> will be performed with
    * <code>optional</code> value; otherwise, the <code>elseAction</code> is performed.
    * 
    * @param optional the {@link Optional} to process.
    * @param presentAction {@link Consumer} performed with the <code>optional</code> contents when present.
    * @param elseAction {@link Runnable} performed when the <code>optional</code> is empty.
    * @implNote With Java 9+ this method will no longer be necessary as the method Optional::ifPresentOrElse is
    * available.
    */

   private static void ifPresentOrElse(Optional<CatProject> optional, Consumer<CatProject> presentAction, Runnable elseAction) {
      //TODO: remove this method and refactor with Java 9+
      if (optional.isPresent()) {
         presentAction.accept(optional.get());
      } else {
         elseAction.run();
      }
   }

   /**
    * Saves the cache of {@link CatProject}s by name and by {@link IProject}.
    */

   protected CatProjectCache catProjectCache;

   /**
    * Creates an unititialized {@link CatProjectManager} instance.
    * 
    * @implNote The {@link CatPlugin} constructor creates the {@link CatProjectManager}. The
    * {@link CatProjectManager#start} method is called by the {@link CatPlugin#start} bundle activator to initialize the
    * {@link CatProjectManager}.
    */

   public CatProjectManager() {
      this.catProjectCache = null;
   }

   /**
    * Adds the <code>catProject</code> to the cache replacing any {@link CatProject} instances that were for the same
    * {@link IProject}.
    * 
    * @param catProject the {@link CatProject} instance to cache.
    */

   public void addCatProject(CatProject catProject) {
      this.catProjectCache.add(catProject);
   }

   /**
    * No action by the {@link CatProjectManager} is performed when the workspace save is completed.
    * <p>
    * {@inheritDoc}
    */

   @Override
   public void doneSaving(ISaveContext context) {
      // nothing to do
   }

   public Optional<CatProject> getCatProject(IProject project) {
      return this.catProjectCache.get(project);
   }

   /**
    * No action by the {@link CatProjectManager} is performed in preparation for a workspace save.
    * <p>
    * {@inheritDoc}
    */

   @Override
   public void prepareToSave(ISaveContext context) throws CoreException {
      // nothing to do
   }

   /**
    * Removes the {@link CatProject} associated with the {@link IProject} from the cache.
    * 
    * @param project the {@link IProject} whose associated {@link CatProject} is to be removed from the cache.
    * @return an {@link Optional} containing the removed {@link CatProject} when the cache contained an association;
    * otherwise, an empty {@link Optional}.
    */
   public Optional<CatProject> removeCatProject(IProject project) {
      return this.catProjectCache.remove(project);
   }

   /**
    * The {@link CatProjectManager} does not support rollback operations.
    * <p>
    * {@inheritDoc}
    */

   @Override
   public void rollback(ISaveContext context) {
      // nothing to do
   }

   /**
    * Saves the cache to the CAT Plug-In state file for a workspace save operation.
    * <p>
    * {@inheritDoc}
    */

   @Override
   public void saving(ISaveContext context) throws CoreException {
      this.catProjectCache.save();
   }

   /**
    * Initializes the {@link CatProjectManager} as follows:
    * <ul>
    * <li>Creates the {@link CatProjectCache}.</li>
    * <li>Adds the {@link CatProjectManager} to the workspace save participant list.</li>
    * <li>Reads the CAT Plug-In state file and initializes the {@link CatProjectCache} from it's contents.</li>
    * <li>Creates and starts a background job to synchronize the {@link CatProjectCache} with the current state of the
    * projects in the workspace.</li>
    * </ul>
    * 
    * @throws CatPluginException when unable to initialize the {@link CatProjectManager}.
    */

   public void start() {
      try {
         this.catProjectCache = new CatProjectCache();
         IWorkspace workspace = ResourcesPlugin.getWorkspace();
         workspace.addSaveParticipant(CatPlugin.getIdentifier(), this);
         File stateLocationFile = CatPlugin.getStateLocationFile();
         if (stateLocationFile.canRead()) {
            CatProjectsInfo.read(stateLocationFile, this.catProjectCache::add);
         }
         this.updateProjectNatures();
      } catch (Exception e) {
         //@formatter:off
         throw new 
            CatPluginException
               (
                  CatErrorCode.InternalError,
                  "Failed to start the CAT Project Manager." + "\n",
                  e
               );
         //@formatter:off
      }
   }

   /**
    * Removes the {@link CatProjectManager} from the workspace's list of save participants.
    */
   
   public void stop() {
      this.catProjectCache = null;
      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      workspace.removeSaveParticipant(CatPlugin.getIdentifier());
   }

   /**
    * When a {@link CatProject} is associated with the <code>project</code> it is updated from the project's {@value Constants#catProjectInfoFileName} file
    * and updated in the cache if changed; otherwise, a new {@link CatProject} is created for the <code>project</code> and cached.
    * @param project the workspace project with a {@link CatNature} to be updated or cached.
    * @implNote This method is only called with <code>project</code>'s that are known to have a {@link CatNature}.
    */

   private void synchronizeProject(IProject project) {
      //TODO: refactor with Java 9+
      //@formatter:off
      ifPresentOrElse
         (
            this.catProjectCache.get(project),
            ( catProject ) -> CatProject.updateCatProject(catProject,project).ifPresent(this.catProjectCache::add),
            () -> this.catProjectCache.add( CatProject.create( project ) )
         );
      //@formatter:on
   }

   /**
    * This method performs the synchronization of the cache and preferences.
    * 
    * @param projectsWithNature a {@link Map} of all projects in the workspace with the {@link CatNature}.
    * @param projectsWithoutNature an {@link Map} of all projects in the workspace without the {@link CatNature}.
    * @see {@link #updateProjectNatures()}.
    * @implNote All exceptions are caught and then logged with the {@link StatusManager}.
    */

   private void synchronizeProjectsWithPreferences(Map<String, IProject> projectsWithNature, Map<String, IProject> projectsWithoutNature) {

      /*
       * Get a comma separated list of the names of the projects that should have the CAT Nature from the preferences.
       * Projects not on this list with the CAT Nature will have the CAT Nature removed.
       */

      String projectPreferenceString = Preference.JTS_PROJECTS.get();

      if (Objects.nonNull(projectPreferenceString) && !projectPreferenceString.isEmpty()) {

         String[] projectNames = projectPreferenceString.split(",");

         /*
          * Loop the names of projects that should be configured with the CAT Nature.
          */

         for (String projectName : projectNames) {

            IProject project;

            if (Objects.nonNull(project = projectsWithNature.get(projectName))) {

               /*
                * Project has CatNature
                */

               /*
                * Move CatNature to first position, if it is not first
                */

               int position = Projects.position(project, CatPlugin.getIdentifier());
               if (position > 0) {
                  Projects.moveNatureToTheFirstPosition(project, position);
               }

               /*
                * Update the compiler options for the project
                */

               this.getCatProject(project).ifPresent(CatProject::update);

               /*
                * Remove updated project from the set of projects with the CAT Nature. The remaining projects after this
                * loop need to have the CAT Nature removed.
                */

               projectsWithNature.remove(projectName);

            } else if (Objects.nonNull(project = projectsWithoutNature.get(projectName))) {

               /*
                * Add the CatNature to the project. This will invoke the configure method to set the compiler options
                * and add the project to the cache.
                */

               Projects.addNature(project, CatPlugin.getCatNatureIdentifier());

            }
         }
      }

      /*
       * Leftover projects in projectsWithNature need to have the CAT Nature removed.
       */

      for (IProject project : projectsWithNature.values()) {

         /*
          * Remove the CatNature from the project. This will invoke the deconfigure method to remove the compiler
          * options from the project and remove the project from the cache.
          */

         Projects.removeProjectNature(project, CatPlugin.getCatNatureIdentifier());
      }

      List<String> names = this.catProjectCache.getNames();

      names.sort((a, b) -> a.compareTo(b));
      String nameCommaList = names.stream().collect(Collectors.joining(","));
      Preference.JTS_PROJECTS.set(nameCommaList);
      CatPlugin.savePreferences();
   }

   /**
    * This method performs the synchronization of the cache, projects, and preferences.
    * 
    * @see {@link #updateProjectNatures()}.
    * @implNote All exceptions are caught and then logged with the {@link StatusManager}.
    */

   synchronized private void synchronizeProjects() {

      try {

         /*
          * Get all workspace projects separated by those with the CAT Nature and those without.
          */

         //@formatter:off
         Pair<HashMap<String, IProject>, HashMap<String, IProject>> pair = 
            Projects.getProjectsForNature
               (
                  CatPlugin.getCatNatureIdentifier(), 
                  HashMap::new,
                  (map, project) -> map.put(project.toString(),project)
               );
         HashMap<String, IProject> projectsWithNature = pair.getFirst();
         HashMap<String, IProject> projectsWithoutNature = pair.getSecond();
         //@formatter:on

         /*
          * Ensure cached data matches project data for projects with the CatNature. Project data takes precedence over
          * cached data.
          */

         projectsWithNature.values().forEach(this::synchronizeProject);

         /*
          * Remove all projects that do not have the CatNature from the cache.
          */

         projectsWithoutNature.values().forEach(this.catProjectCache::remove);

         /*
          * Ensure projects specified in the preferences have the CatNature and remove the CatNature from projects not
          * specified in the preferences.
          */

         this.synchronizeProjectsWithPreferences(projectsWithNature, projectsWithoutNature);

         /*
          * Persist preferences and cache
          */

         CatPlugin.savePreferences();
         this.catProjectCache.save();

      } catch (CatPluginException cpe) {
         cpe.log();
      } catch (Exception e) {
         //@formatter:off
         CatPluginException synchronizeProjectsException =
            new CatPluginException
                   (
                      CatErrorCode.InternalError,
                      "Failed to synchronize CAT Project Manager with preferences." + "\n",
                      e
                   );
         //@formatter:on
         synchronizeProjectsException.log();
      }
   }

   /**
    * Starts a background job to synchronize the cache, project, and preferences as follows:
    * <ul>
    * <li>The {@value Constants#catProjectInfoFileName} file for all projects with the {@link CatNature} is read and the
    * cache is updated with any changes to the {@link CatProjectInfo} that were found.</li>
    * <li>Projects without the {@link CatNature} that are also in the cache are removed from the cache.</li>
    * <li>Any project listed in the preferences that is not in the cache will have the {@link CatNature} added to the
    * project and the project is added to the cache.</li>
    * <li>Any project in the cache that is not also listed in the preferences will have the {@link CatNature} removed
    * and also be removed from the cache.</li>
    * </ul>
    * At the completion of the job, the preferences and cache are persisted.
    */

   public void updateProjectNatures() {
      //@formatter:off
      Job job = 
         Job.create
            (
               "Update CAT Project Manager Cache",
               (ICoreRunnable) monitor -> this.synchronizeProjects()
            );
      //@formatter:on
      job.schedule();
   }
}

/*********************************************************************
 * Copyright (c) 2020 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.ote.simple.oteide.product.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IExportedPreferences;
import org.eclipse.core.runtime.preferences.IPreferenceFilter;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.PreferenceFilterEntry;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.ui.IStartup;

/**
 * Loads a custom Eclipse .epf preferences file on startup. Forces Save Actions preferences on startup or when a new
 * workspace has been created.
 * 
 * @author Dominic Guss
 */
public class LoadPreferences implements IStartup {
   private final String PREFERENCES_FILE_PATH =
      System.getProperty("user.dir") + File.separator + "OSEE_Java_Preferences.epf";

   Job saveActionsJob = new Job("Save Actions Preferences Job") {
      @Override
      public IStatus run(IProgressMonitor monitor) {
         IEclipsePreferences jdtUiPrefs = InstanceScope.INSTANCE.getNode("org.eclipse.jdt.ui");
         jdtUiPrefs.putBoolean("editor_save_participant_org.eclipse.jdt.ui.postsavelistener.cleanup", true);
         jdtUiPrefs.putBoolean("sp_cleanup.format_source_code", true);
         jdtUiPrefs.putBoolean("sp_cleanup.organize_imports", true);
         jdtUiPrefs.putBoolean("sp_cleanup.on_save_use_additional_actions", true);

         IEclipsePreferences coreRuntimePrefs = InstanceScope.INSTANCE.getNode("org.eclipse.core.runtime");
         coreRuntimePrefs.put("line.separator", "\n"); // Unix style line endings (Windows is \r\n)

         IEclipsePreferences jdtCorePrefs = InstanceScope.INSTANCE.getNode("org.eclipse.jdt.core");
         jdtCorePrefs.putFloat("org.eclipse.jdt.core.compiler.codegen.targetPlatform", 1.8F);
         jdtCorePrefs.putFloat("org.eclipse.jdt.core.compiler.compliance", 1.8F);
         jdtCorePrefs.putFloat("org.eclipse.jdt.core.compiler.source", 1.8F);

         return Status.OK_STATUS;
      }
   };

   @Override
   public void earlyStartup() {
      saveActionsJob.schedule();

      IPreferenceFilter[] ipFilters = new IPreferenceFilter[1];

      ipFilters[0] = new IPreferenceFilter() {
         @Override
         public String[] getScopes() {
            return new String[] {InstanceScope.SCOPE, ConfigurationScope.SCOPE};
         }

         @Override
         public Map<String, PreferenceFilterEntry[]> getMapping(String scope) {
            return null;
         }
      };

      IPreferencesService service = Platform.getPreferencesService();
      InputStream inputStream = null;
      IExportedPreferences prefs = null;

      try {
         inputStream = new FileInputStream(new File(PREFERENCES_FILE_PATH));
      } catch (IOException ex) {
         OseeCoreException.wrap(ex);
      }
      try {
         prefs = service.readPreferences(inputStream);
      } catch (CoreException ex) {
         OseeCoreException.wrap(ex);
      }
      try {
         service.applyPreferences(prefs, ipFilters);
      } catch (CoreException ex) {
         OseeCoreException.wrap(ex);
      }
   }
}

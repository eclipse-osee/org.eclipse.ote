/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.simple.oteide.product.load;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.ui.IStartup;

/*
 * Class to ensure custom preferences are the default option when starting from a new
 * workspace. Also ensures user changes of these optional settings are persisted,
 * should the user change their preferences. When utilizing the plugin_customization.ini
 * to load preferences, this class also overcomes a known bug relating to saving
 * participant cleanup preferences described here:
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=281487
 */

/**
 * @author Dominic Guss
 */
public class PersistPreferences implements IStartup {

   private final Map<String, String> buggedNamespace = new HashMap<String, String>();

   @Override
   public void earlyStartup() {

      IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
      if (projects.length < 1) { // Ensure preferences are utilized in buggedNamespace on new workspace creation
         // Insert any relevant bugged namespaces:
         buggedNamespace.put("org.eclipse.jdt.ui", "sp_cleanup");
         final Iterator<Entry<String, String>> it = buggedNamespace.entrySet().iterator();

         while (it.hasNext()) {
            final Map.Entry<String, String> pair = it.next();
            final String namespace = pair.getKey();
            final String[] filters = pair.getValue().split("|");
            final IEclipsePreferences pluginCustomizationPreferences = DefaultScope.INSTANCE.getNode(namespace);
            final IEclipsePreferences workspacePreferences = InstanceScope.INSTANCE.getNode(namespace);

            try {
               for (final String key : pluginCustomizationPreferences.keys()) {
                  String defaultPluginCustomizationValue = pluginCustomizationPreferences.get(key, "");
                  if (Strings.isValid(defaultPluginCustomizationValue) && (filters == null || startsWithAny(key, filters))) {
                     workspacePreferences.put(key, defaultPluginCustomizationValue);
                  }
               }
               workspacePreferences.flush();
            } catch (final Exception e) {
               e.printStackTrace();
            }
            it.remove();
         }
      }
   }

   private boolean startsWithAny(String key, String[] values) {
      for (String value : values) {
         return key.startsWith(value);
      }
      return false;
   }
}

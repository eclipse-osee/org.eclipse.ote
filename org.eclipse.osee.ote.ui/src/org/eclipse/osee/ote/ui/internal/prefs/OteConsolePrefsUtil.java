/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.internal.prefs;

import org.eclipse.osee.ote.ui.internal.TestCoreGuiPlugin;

/**
 * @author Michael P. Masterson
 */
public class OteConsolePrefsUtil {

   public static int getInt(OteConsolePreferences bufferLimit) {
      String propKey = bufferLimit.getPropKey();
      lateLoadDefaults(propKey);
      return TestCoreGuiPlugin.getDefault().getPreferenceStore().getInt(propKey);
   }

   public static String getString(OteConsolePreferences bufferLimit) {
      String propKey = bufferLimit.getPropKey();
      lateLoadDefaults(propKey);
      return TestCoreGuiPlugin.getDefault().getPreferenceStore().getString(propKey);
   }
   
   public static boolean getBoolean(OteConsolePreferences bufferLimit) {
      String propKey = bufferLimit.getPropKey();
      lateLoadDefaults(propKey);
      return TestCoreGuiPlugin.getDefault().getPreferenceStore().getBoolean(propKey);
   }
   
   public static void setInt(OteConsolePreferences preference, int value) {
      TestCoreGuiPlugin.getDefault().getPreferenceStore().setValue(preference.getPropKey(), value);
   }
   
   public static void setString(OteConsolePreferences preference, String value) {
      TestCoreGuiPlugin.getDefault().getPreferenceStore().setValue(preference.getPropKey(), value);
   }
   
   public static void setBoolean(OteConsolePreferences preference, boolean value) {
      TestCoreGuiPlugin.getDefault().getPreferenceStore().setValue(preference.getPropKey(), value);
   }
   
   private static void lateLoadDefaults(String keyStr) {
      TestCoreGuiPlugin plugin = TestCoreGuiPlugin.getDefault();
      String defaultString = plugin.getPreferenceStore().getDefaultString(keyStr);
      if(defaultString == null || defaultString.length() <= 0) {
         TestCoreGuiPlugin.setDefaultPreferences();
      }
   }

}

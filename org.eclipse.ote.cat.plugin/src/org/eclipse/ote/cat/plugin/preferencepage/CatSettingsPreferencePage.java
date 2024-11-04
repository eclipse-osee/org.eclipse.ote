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

import org.eclipse.ote.cat.plugin.CatPlugin;

/**
 * Implements the top level preference page for the CAT Plug-in settings. This class is instantiated by the Eclipse UI
 * framework before the preference page is displayed.
 * 
 * @author Loren K. Ashley
 */

public class CatSettingsPreferencePage extends AbstractCatPreferencePage {

   /**
    * Creates the CAT Settings preference page.
    */

   public CatSettingsPreferencePage() {
      super(PreferencePage.CAT_SETTINGS);
      this.setDescription(this.preferencePage.getPageTitle());
   }

   /**
    * Saves the field editor values in the preference store, saves the preference store, and updates the project natures
    * according to the {@link Preference#JTS_PROJECTS} preference.
    */

   @Override
   public boolean performOk() {

      /*
       * Saves preferences on this preference page to the preference store.
       */

      boolean status = super.performOk();

      if (status == false) {
         return false;
      }

      /*
       * Validate the new page values in the preference store and restore the original values when any are invalid.
       */

      status = Preference.validateValues(this.preferencePage, this.preferenceValues);

      if (status == false) {
         return false;
      }

      /*
       * Update project natures
       */

      CatPlugin.getCatProjectManager().updateProjectNatures();

      return true;
   }

}

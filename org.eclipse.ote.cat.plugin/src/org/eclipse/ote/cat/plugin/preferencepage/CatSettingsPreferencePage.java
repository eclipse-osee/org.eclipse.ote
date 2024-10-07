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

}

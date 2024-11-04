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

import java.util.Objects;

/**
 * Enumeration of the preference pages used for CAT preferences.
 * 
 * @see
 * <ul>
 * <li>{@link PreferencePage#CAT_SETTINGS CAT_SETTINGS}</li>
 * <li>{@link PreferencePage#PLE_CONFIGURATION_LOADER PLE_CONFIGURATION_LOADER}</li>
 * </ul>
 */

public enum PreferencePage {

   /**
    * The top level preference page for configuration of the CAT annotation processor and selection of the PLE
    * Configuration.
    */

   CAT_SETTINGS("Compiler Applicability Tool Settings"),

   /**
    * Preference sub-page for loading PLE Configurations from an OPLE server.
    */

   PLE_CONFIGURATION_LOADER("PLE Configuration Loader");

   /**
    * Save the page title used for the preference page.
    */

   private String pageTitle;

   /**
    * Create a new {@link PreferencePage} enumeration member.
    * 
    * @param pageTitle the title of the preference page represented by the enumeration member.
    */

   private PreferencePage(String pageTitle) {
      assert Objects.nonNull(pageTitle) : "Page::new, parameter \"pageTitle\" cannot be null.";
      this.pageTitle = pageTitle;
   }

   /**
    * Gets the plug-in preference page title.
    * 
    * @return preference page title.
    */

   public String getPageTitle() {
      return this.pageTitle;
   }

};

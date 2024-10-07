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
 * Implements a CAT Plug-in preference sub-page for the selection of the PLE Configuration cache and down loading PLE
 * Configurations from an OPLE server.
 * 
 * @author Loren K. Ashley
 */

public class PleConfigurationCachePreferencePage extends AbstractCatPreferencePage {

   /**
    * Creates the PLE Configuration Cache preference page.
    */

   public PleConfigurationCachePreferencePage() {
      super(PreferencePage.PLE_CONFIGURATION_CACHE);
      this.setDescription(this.preferencePage.getPageTitle());
   }

}

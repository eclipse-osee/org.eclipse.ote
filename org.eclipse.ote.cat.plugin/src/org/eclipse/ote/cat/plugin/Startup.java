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

package org.eclipse.ote.cat.plugin;

import org.eclipse.ui.IStartup;

/**
 * CAT Plug-In class that is loaded once the workspace is ready which will cause the CAT Plug-In bundle to be activated.
 * 
 * @author Loren K. Ashley
 */

public class Startup implements IStartup {

   /**
    * Invocation of this method will cause the CAT Plug-In bundle to be activated. The method itself performs no action.
    * <p>
    * {@inheritDoc}
    */

   @Override
   public void earlyStartup() {
      //No action necessary
   }

}

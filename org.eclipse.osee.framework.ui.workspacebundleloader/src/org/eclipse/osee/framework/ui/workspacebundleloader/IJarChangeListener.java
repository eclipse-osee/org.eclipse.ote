/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.workspacebundleloader;

import java.net.URL;

/**
 * @author Robert A. Fisher
 */
public interface IJarChangeListener<T extends JarCollectionNature> {

   /**
    * Called for each addition of bundle
    */
   public void handleBundleAdded(URL url);

   /**
    * Called for each change of bundle
    */
   public void handleBundleChanged(URL url);

   /**
    * Called for each removal of bundle
    */
   public void handleBundleRemoved(URL url);

   /**
    * Called after all add/change/remove methods have been invoked for a given delta.
    */
   public void handlePostChange();

   /**
    * Called just before a project with the nature is closed
    */
   public void handleNatureClosed(T nature);
}

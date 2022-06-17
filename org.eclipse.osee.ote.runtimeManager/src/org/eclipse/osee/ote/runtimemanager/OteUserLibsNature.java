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

package org.eclipse.osee.ote.runtimemanager;

import java.util.Collection;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.ui.workspacebundleloader.JarCollectionNature;

public class OteUserLibsNature extends JarCollectionNature {
   public static final String NATURE_ID = "org.eclipse.osee.ote.runtimeManager.OteUserLibsNature";
   private static final String BUNDLE_PATH_ATTRIBUTE = "OteBundlePath";

   public OteUserLibsNature() {
      super(BUNDLE_PATH_ATTRIBUTE);
   }

   public static Collection<OteUserLibsNature> getWorkspaceProjects() throws CoreException {
      return getWorkspaceProjects(NATURE_ID, OteUserLibsNature.class);
   }
}

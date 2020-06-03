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

import java.util.Collection;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Robert A. Fisher
 * @author Andrew M. Finkbeiner
 */
public class WorkspaceStarterNature extends JarCollectionNature {
   public static final String NATURE_ID = "org.eclipse.osee.framework.ui.workspacebundleloader.WorkspaceStarterNature";
   static final String BUNDLE_PATH_ATTRIBUTE = "WorkspaceBundlePath";

   public WorkspaceStarterNature() {
      super(BUNDLE_PATH_ATTRIBUTE);
   }

   public static Collection<WorkspaceStarterNature> getWorkspaceProjects() throws CoreException {
      return getWorkspaceProjects(NATURE_ID, WorkspaceStarterNature.class);
   }
}

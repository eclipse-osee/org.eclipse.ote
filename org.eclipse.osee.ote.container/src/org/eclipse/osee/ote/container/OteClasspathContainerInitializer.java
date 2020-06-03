/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ote.container;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class OteClasspathContainerInitializer extends ClasspathContainerInitializer {

   public OteClasspathContainerInitializer() {
   }

   @Override
   public void initialize(IPath containerPath, IJavaProject project) throws CoreException {

      OteClasspathContainer oteClasspathContainer = new OteClasspathContainer(containerPath, project);
      JavaCore.setClasspathContainer(containerPath, new IJavaProject[] {project},
         new IClasspathContainer[] {oteClasspathContainer}, null);
   }

}

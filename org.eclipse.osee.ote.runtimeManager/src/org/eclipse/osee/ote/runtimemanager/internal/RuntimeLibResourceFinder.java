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

package org.eclipse.osee.ote.runtimemanager.internal;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.ote.classserver.ResourceFinder;
import org.eclipse.osee.ote.core.BundleInfo;
import org.eclipse.osee.ote.runtimemanager.SafeWorkspaceTracker;

/**
 * @author Robert A. Fisher
 */
public class RuntimeLibResourceFinder extends ResourceFinder {
   private final SafeWorkspaceTracker safeWorkspaceTracker;

   public RuntimeLibResourceFinder(SafeWorkspaceTracker safeWorkspaceTracker) {
      super();
      this.safeWorkspaceTracker = safeWorkspaceTracker;
   }

   @Override
   public byte[] find(String path) throws IOException {
      try {
         Collection<BundleInfo> runtimeLibs = safeWorkspaceTracker.getRuntimeLibs();
         for (BundleInfo info : runtimeLibs) {
            if (info.getSymbolicName().equals(path)) {
               return Lib.inputStreamToBytes(new FileInputStream(info.getFile()));
            }
         }
      } catch (CoreException ex) {
         // TODO
         ex.printStackTrace();
      }
      return null;
   }

   @Override
   public void dispose() {
   }
}

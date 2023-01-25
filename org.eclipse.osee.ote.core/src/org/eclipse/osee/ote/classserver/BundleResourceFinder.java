/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ote.classserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.ote.classserver.ResourceFinder;
import org.eclipse.osee.ote.core.BundleInfo;

public class BundleResourceFinder extends ResourceFinder {

   private final List<BundleInfo> bundleInfo;

   public BundleResourceFinder(List<BundleInfo> bundleInfo) {
      super();
      this.bundleInfo = bundleInfo;
   }

   @Override
   public byte[] find(String path) throws IOException {
      for (BundleInfo info : bundleInfo) {
         if (info.getSymbolicName().equals(path) || info.getFile().getName().equals(path)) {
            return Lib.inputStreamToBytes(new FileInputStream(info.getFile()));
         } 
      }
      return null;
   }

   @Override
   public void dispose() {
      //
   }
}

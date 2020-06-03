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

package org.eclipse.osee.ote.core.environment.interfaces;

public class OteUtil {

   public static String generateBundleVersionString(String bundleSpecificVersion, String symbolicName, String version, String md5) {
      StringBuilder sb = new StringBuilder();
      if (bundleSpecificVersion != null) {
         sb.append(bundleSpecificVersion);
         sb.append("_");
      }
      sb.append(symbolicName);
      sb.append("_");
      sb.append(version);
      sb.append("_");
      sb.append(md5);
      return sb.toString();
   }
}
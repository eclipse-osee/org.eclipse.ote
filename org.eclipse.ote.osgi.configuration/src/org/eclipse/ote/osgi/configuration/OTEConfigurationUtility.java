/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.ote.osgi.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.eclipse.ote.services.core.BundleUtility;

/**
 * Provides utility to read in any *.properties files within a folder and provide them via InputStreams
 * 
 * @author Michael P. Masterson
 */
public class OTEConfigurationUtility {

   private OTEConfigurationUtility() {
      // utility
   }

   public static InputStream[] getPropertyFiles(String bundleSymbolicName, String configFolder) {
      Set<String> entries = BundleUtility.findSubEntries(bundleSymbolicName, configFolder);
      List<String> filtered = new ArrayList<String>();
      for (String entry : entries) {
         if (entry.endsWith(".properties")) {
            filtered.add(entry);
         }
      }
      List<URL> urls = BundleUtility.entriesToURLs(bundleSymbolicName, filtered);
      InputStream[] streams = new InputStream[urls.size()];
      for (int i = 0; i < urls.size(); i++) {
         InputStream stream = null;
         try {
            stream = urls.get(i).openStream();
            streams[i] = stream;
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }
      return streams;
   }
}

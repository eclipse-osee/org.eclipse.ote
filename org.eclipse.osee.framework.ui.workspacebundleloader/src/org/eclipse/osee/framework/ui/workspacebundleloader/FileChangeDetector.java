/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.workspacebundleloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 */
public class FileChangeDetector {

   private final ConcurrentHashMap<URL, byte[]> bundleNameToMd5Map;

   public FileChangeDetector() {
      bundleNameToMd5Map = new ConcurrentHashMap<>();
   }

   public boolean isChanged(URL url) {
      byte[] digest = getMd5Checksum(url);
      if (bundleNameToMd5Map.containsKey(url)) {
         // check for bundle binary equality
         if (!Arrays.equals(bundleNameToMd5Map.get(url), digest)) {
            bundleNameToMd5Map.put(url, digest);
            return true;
         } else {
            return false;
         }
      } else {
         bundleNameToMd5Map.put(url, digest);
         return true;
      }
   }

   private byte[] getMd5Checksum(URL url) {
      InputStream in = null;
      byte[] digest = new byte[0];
      try {
         in = url.openStream();
         digest = ChecksumUtil.createChecksum(url.openStream(), "MD5");
      } catch (IOException ex) {
         OseeLog.log(FileChangeDetector.class, Level.SEVERE, ex);
      } catch (NoSuchAlgorithmException ex) {
         OseeLog.log(FileChangeDetector.class, Level.SEVERE, ex);
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (IOException ex) {
               OseeLog.log(FileChangeDetector.class, Level.SEVERE, ex);
            }
         }
      }
      return digest;
   }

   public boolean remove(URL url) {
      bundleNameToMd5Map.remove(url);
      return true;
   }

   public void clear() {
      bundleNameToMd5Map.clear();
   }

}

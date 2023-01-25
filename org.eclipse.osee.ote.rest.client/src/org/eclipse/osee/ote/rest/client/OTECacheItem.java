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
package org.eclipse.osee.ote.rest.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;

public class OTECacheItem {
   private File file;
   private String md5;
   
   public OTECacheItem(File file, String md5){
      this.file = file;
      this.md5 = md5;
   }
   
   public OTECacheItem(File file) throws FileNotFoundException, Exception{
      this.file = file;
      md5 = ChecksumUtil.createChecksumAsString(new FileInputStream(file), "MD5");
   }
   
   public File getFile() {
      return file;
   }
   
   public String getMd5() {
      return md5;
   }
   
}

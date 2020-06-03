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

package org.eclipse.ote.client.ui.core.widgets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Michael P. Masterson
 */
public class UnzipJob extends Job {

   private File sourceZip;
   private File destFolder;

   /**
    * @param sourceZip
    * @param destFolder
    */
   public UnzipJob(File sourceZip, File destFolder) {
      super("Unzipping " + sourceZip.getAbsolutePath());
      this.sourceZip = sourceZip;
      this.destFolder = destFolder;
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      try {
         unzip(sourceZip, destFolder, monitor);
         monitor.done();
      }
      catch (IOException ex) {
         ex.printStackTrace();
      }
      return Status.OK_STATUS;
   }
   
   private boolean unzip(File zipFile, File directory, IProgressMonitor monitor) throws IOException {
      int BUFFER_LEN = 2048;
      BufferedOutputStream dest = null;
      BufferedInputStream is = null;
      ZipEntry entry = null;
      SubMonitor subMonitor = SubMonitor.convert(monitor, getZipSize(zipFile));
      subMonitor.setTaskName("Unzipping " + zipFile.getName());
      try {
          ZipFile zipfile = new ZipFile(zipFile.getAbsolutePath());
          Enumeration<? extends ZipEntry> e = zipfile.entries();
          while (e.hasMoreElements()) {
              entry = e.nextElement();
              is = new BufferedInputStream(zipfile.getInputStream(entry));
              int count;
              byte data[] = new byte[BUFFER_LEN];
              File fileDir = new File(directory, entry.getName());
              if (entry.isDirectory()) {
                  fileDir.mkdirs();
                  continue;
              } else {
                  fileDir.getParentFile().mkdirs();
              }

              if (!fileDir.exists() || fileDir.exists() && fileDir.canWrite()) {
                  FileOutputStream fos = new FileOutputStream(fileDir.getAbsolutePath());
                  dest = new BufferedOutputStream(fos, BUFFER_LEN);
                  try {
                      while ((count = is.read(data, 0, BUFFER_LEN)) != -1) {
                          dest.write(data, 0, count);
                          subMonitor.worked(count);
                          if (subMonitor.isCanceled()) {
                              return false;
                          }
                      }
                  } finally {
                          dest.flush();
                          Lib.close(dest);
                  }
              }

              if (fileDir.getAbsolutePath().endsWith(".lnk")) {
                  if (fileDir.canWrite()) {
                      fileDir.setReadOnly();
                  }
              }
          }
          zipfile.close();
      } catch (RuntimeException ex) {
          String information = "ZipFile: " + (zipFile.getAbsolutePath()) + "\n"
                  + "DestinationDir: " + (directory != null ? directory.getAbsolutePath() : "NULL") + "\n"
                  + "Entry Processed: " + (entry != null ? entry.toString() : "NULL") + "\n";
          throw new IOException(information + ex.getMessage());
      } finally {
          Lib.close(is);
      }
      return true;
  }
   
   private int getZipSize(File zipFile) throws IOException {
      int size = 0;
      ZipEntry entry = null;
      ZipFile zf = null;
      try {
          zf = new ZipFile(zipFile);
          Enumeration<? extends ZipEntry> e = zf.entries();
          while (e.hasMoreElements()) {
              entry = e.nextElement();
              size += entry.getSize();
          }
      } finally {
          if (zipFile != null) {
              zf.close();
          }
      }
      return size;
  }

}

/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.test.manager.models;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.ws.AWorkspace;
import org.eclipse.osee.ote.ui.test.manager.internal.TestManagerPlugin;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class FileModel {

   private File file = null;
   private IFile iFile = null;
   private long lastModified = 0;
   private String path = "";
   private String rawFilename = "";
   private String text = null;

   public FileModel(String rawFilename) {
      this.rawFilename = rawFilename;
      if (getIFile() != null) {
         lastModified = getIFile().getModificationStamp();
      }
   }

   public boolean exists() {
      return getFile().exists();
   }

   /**
    * @return Returns the file.
    */
   public File getFile() {
      if (file == null) {
         file = new File(rawFilename);
      }
      return file;
   }

   /**
    * @return Returns the iFile for the given local data {@link #rawFilename}.  You may still have to check if the file actually exists.
    */
   public IFile getIFile() {
      if (iFile == null) {
         if (path.equals("")) {
            if (!rawFilename.equals("")) {
               iFile = AWorkspace.getIFile(rawFilename);
               if(iFile == null){
                  IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(org.eclipse.core.filesystem.URIUtil.toURI(rawFilename));
                  if(files.length > 0){
                     iFile = files[0];                     
                  }
               }
            }
         }
      }
      return iFile;
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return new File(rawFilename).getName();
   }

   /**
    * @return Returns the path.
    */
   public String getPath() {
      if (iFile == null) {
         iFile = getIFile();
      }
      if (iFile != null) {
         path = iFile.getFullPath().toString();
      }
      return path;
   }

   /**
    * @return Returns the rawFilename.
    */
   public String getRawFilename() {
      return rawFilename;
   }

   public String getWorkspaceRelativePath() {
      IWorkspace ws = ResourcesPlugin.getWorkspace();
      IFile ifile = ws.getRoot().getFileForLocation(new Path(rawFilename));
      if(ifile == null){
         IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(org.eclipse.core.filesystem.URIUtil.toURI(rawFilename));
         if(files.length > 0){
            ifile = files[0];
         }
      }
      if (!ifile.exists()) {
         return null;
      } else {
         return ifile.getFullPath().toString();
      }
   }

   public String getText() throws IOException {
      if (iFile == null) {
         getIFile();
      }
      if (iFile == null) {
         return "";
      }
      if (text == null || iFile.getModificationStamp() != lastModified) {
         text = Lib.fileToString(new File(rawFilename));
         OseeLog.log(TestManagerPlugin.class, Level.INFO, "getText: Reading file " + getName());
      } else {
         OseeLog.log(TestManagerPlugin.class, Level.INFO, "getText: Using buffered file " + getName());
      }
      lastModified = iFile.getModificationStamp();
      return text;
   }

   public boolean isModified() {
      if (iFile == null) {
         getIFile();
      }
      if (iFile == null) {
         OseeLog.log(TestManagerPlugin.class, Level.WARNING, "Can't Read iFile");
         return true;
      }
      return iFile.getModificationStamp() != lastModified;
   }

   public void openEditor() {
      if (getIFile() != null) {
         AWorkspace.openEditor(getIFile());
      } else {
         IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(file.getAbsolutePath()));
         IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
         try {
            IDE.openEditorOnFileStore(page, fileStore);
         } catch (PartInitException e) {
            e.printStackTrace();
         }         
      }
   }

   public void openPackageExplorer() {
      OseeLog.log(TestManagerPlugin.class, Level.INFO, "Show in explorer " + getName());
      // Open in Package Explorer and error if can't
      boolean success = AWorkspace.showInPackageExplorer(getIFile());
      //      if(!success){
      //         success = AWorkspace.showInResourceNavigator(getIFile());
      //      }
      if (!success) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Open Error",
            "Can't Show in Explorer\n\n" + getName());
      }
      // As a convenience, open in Navigator, but don't error
      success = AWorkspace.showInResourceNavigator(getIFile());
   }

   /**
    * @param path The path to set.
    */
   public void setPath(String path) {
      this.path = path;
   }

   /**
    * @param rawFilename The rawFilename to set.
    */
   public void setRawFilename(String rawFilename) {
      this.rawFilename = rawFilename;
   }
}
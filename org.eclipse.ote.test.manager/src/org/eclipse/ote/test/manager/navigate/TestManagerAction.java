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
package org.eclipse.ote.test.manager.navigate;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.ote.test.manager.OteTestManagerFactory;
import org.eclipse.ote.test.manager.internal.OteTestManagerPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.ws.AWorkspace;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * @author Donald G. Dunne
 */
public class TestManagerAction extends Action {

   private IFile inputFile;

   public TestManagerAction() {
      super("Open OTE Test Manager");
   }

   @Override
   public void run() {
      // try {

      // // String filePath =
      // OseeData.getValue(OteTestManagerFactory.LAST_OPENED_KEY);
      // if (filePath != null) {
      // inputFile = AWorkspace.fileToIFile(new File(filePath));
      // }
      // if (inputFile == null) {

      // we could find the files and put them in a selection dialog?

      // List<Object> files = new ArrayList<>();
      // files.addAll(AWorkspace.findWorkspaceFileMatch(".*\\.ote"));
      // if (files.isEmpty()) {
      // files.add(new String("No Test Manager files found in the workspace,
      // create a New File?"));
      // }
      // DialogSelectionHelper selection = new
      // DialogSelectionHelper(files.toArray());

      inputFile = null;

      // ElementListSelection
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            ElementListSelectionDialog dlg =
               new ElementListSelectionDialog(Displays.getActiveShell(), new ILabelProvider() {
                  @Override
                  public Image getImage(Object element) {
                     return null;
                  }

                  @Override
                  public String getText(Object element) {
                     if (element instanceof IFile) {
                        return ((IFile) element).getFullPath().toString();
                     } else {
                        return element.toString();
                     }
                  }

                  @Override
                  public void addListener(ILabelProviderListener listener) {
                     // INTENTIONALLY EMPTY BLOCK
                  }

                  @Override
                  public void dispose() {
                     // INTENTIONALLY EMPTY BLOCK
                  }

                  @Override
                  public boolean isLabelProperty(Object element, String property) {
                     return false;
                  }

                  @Override
                  public void removeListener(ILabelProviderListener listener) {
                     // INTENTIONALLY EMPTY BLOCK
                  }
               });
            try {
               List<Object> files = new ArrayList<>();
               files.addAll(AWorkspace.findWorkspaceFileMatch(".*\\.ote"));
               if (files.isEmpty()) {
                  files.add(new String("Create a new configuration file (none found in workspace)"));
               }
               dlg.setMultipleSelection(false);
               dlg.setElements(files.toArray());
               dlg.setInitialSelections(new Object[] {files.get(0)});
               dlg.setTitle("Test Manager Configuration Files");
               dlg.setMessage("Select a test manager configuration file to open.");
               dlg.open();
               Object[] results = dlg.getResult();
               if (results != null) {
                  System.out.println();

                  if (results.length > 0) {
                     if (results[0] instanceof IFile) {
                        inputFile = (IFile) results[0];
                     } else if (results[0] instanceof String) {
                        PipedOutputStream pos = new PipedOutputStream();
                        PrintStream ps = new PrintStream(pos);
                        InputStream in = new PipedInputStream(pos);
                        ps.println(getDefaultTMData());
                        ps.close();
                        try {
                           inputFile = OseeData.getIFile(
                              OteTestManagerFactory.TEST_MANAGER_FILENAME + "." + OteTestManagerFactory.TEST_MANAGER_EXTENSION,
                              in);
                        } catch (OseeCoreException ex) {
                           OseeLog.log(OteTestManagerPlugin.class, Level.SEVERE, ex);
                        }

                     }
                  }

               }
            } catch (IOException ex) {
               OseeLog.log(OteTestManagerPlugin.class, Level.SEVERE, ex);
            }
         }
      });

      // int index = selection.getSelectionIndex();
      // if (files.isEmpty() && index != -1) {
      // PipedOutputStream pos = new PipedOutputStream();
      // PrintStream ps = new PrintStream(pos);
      // InputStream in = new PipedInputStream(pos);
      // ps.println(defaultTMData);
      // ps.close();
      // inputFile =
      // OseeData.getIFile(OteTestManagerFactory.TEST_MANAGER_FILENAME + "." +
      // OteTestManagerFactory.TEST_MANAGER_EXTENSION, in);
      // }
      // else if (index != -1) {
      // Object obj = files.get(index);
      // if (obj instanceof IFile) {
      // inputFile = (IFile) obj;
      // }
      // else {
      // OseeLog.logf(Activator.class, Level.WARNING, "%s was not an Ifile so we
      // can't open it.", obj.toString());
      // }
      // }
      //
      if (inputFile != null) {

         IEditorInput editorInput = new TestManagerEditorInput(inputFile);
         IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
         try {
            page.openEditor(editorInput, OteTestManagerFactory.EDITOR_ID);
         } catch (PartInitException ex) {
            OseeLog.log(OteTestManagerPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   protected String getDefaultTMData() {
      StringBuilder sb = new StringBuilder();
      sb.append("<testManager>\n");
      sb.append("</testManager>\n");
      return sb.toString();
   }
}

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

package org.eclipse.osee.ote.ui.test.manager.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.ote.ui.test.manager.operations.AddIFileToTestManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

public class AddToTestManagerPopupAction implements IWorkbenchWindowActionDelegate {

   public static String[] getSelection() {
      List<String> selection = new ArrayList<>();
      ISelection sel1 = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
      if (sel1 instanceof StructuredSelection) {
         Iterator<?> i = ((StructuredSelection) sel1).iterator();

         while (i.hasNext()) {
            Object obj = i.next();
            if (obj instanceof IResource) {
               IResource resource = (IResource) obj;
               selection.add(resource.getLocation().toOSString());
            } else if (obj instanceof ICompilationUnit) {
               ICompilationUnit resource = (ICompilationUnit) obj;
               selection.add(resource.getResource().getLocation().toOSString());
            } else if (obj instanceof IMember) {
               ICompilationUnit resource = ((IMember) obj).getCompilationUnit();
               if (resource != null) {
                  selection.add(resource.getResource().getLocation().toOSString());
               }
            }
         }
      } else if (sel1 instanceof TextSelection) {
         IEditorPart editorPart =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
         IEditorInput editorInput = editorPart.getEditorInput();
         IFile iFile = null;
         if (editorInput instanceof IFileEditorInput) {
            iFile = ((IFileEditorInput) editorInput).getFile();
            if (iFile != null) {
               selection.add(iFile.getLocation().toOSString());
            }
         }
      }
      return selection.toArray(new String[0]);
   }

   IWorkbenchWindow activeWindow = null;

   // IWorkbenchWindowActionDelegate method
   @Override
   public void dispose() {
      // nothing to do
   }

   // IWorkbenchWindowActionDelegate method
   @Override
   public void init(IWorkbenchWindow window) {
      activeWindow = window;
   }

   @Override
   public void run(IAction proxyAction) {
      String[] files = getSelection();
      if (files.length == 0) {
         AWorkbench.popup("ERROR", "Can't retrieve file");
         return;
      }
      AddIFileToTestManager.getOperation().addIFileToScriptsPage(files);
   }

   // IActionDelegate method
   @Override
   public void selectionChanged(IAction proxyAction, ISelection selection) {

   }
}
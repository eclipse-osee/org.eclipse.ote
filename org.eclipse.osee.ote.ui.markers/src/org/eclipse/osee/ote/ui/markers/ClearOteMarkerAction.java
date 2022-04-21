/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ote.ui.markers;

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public class ClearOteMarkerAction implements IEditorActionDelegate {

   ArrayList<IResource> selections;
   private IEditorPart editor;

   public ClearOteMarkerAction() {
      selections = new ArrayList<>();
   }

   @Override
   public void run(IAction action) {
      for (IResource resource : selections) {
         MarkerPlugin.findAndRemoveOteMarkers(resource);
      }
   }

   @Override
   public void selectionChanged(IAction action, ISelection selection) {
      selections.clear();
      if (selection instanceof StructuredSelection) {
         Iterator<?> i = ((StructuredSelection) selection).iterator();
         while (i.hasNext()) {
            Object obj = i.next();
            if (obj instanceof IResource) {
               IResource resource = (IResource) obj;
               selections.add(resource);
            }
         }
      } else {
         Object obj = editor.getEditorInput().getAdapter(IResource.class);
         if (obj != null) {
            selections.add((IResource) obj);
         }
      }
   }

   @Override
   public void setActiveEditor(IAction action, IEditorPart targetEditor) {
      this.editor = targetEditor;
   }

}

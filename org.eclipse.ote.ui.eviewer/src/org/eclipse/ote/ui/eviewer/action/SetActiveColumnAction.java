/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.eviewer.action;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.ote.ui.eviewer.view.ViewerColumn;
import org.eclipse.ote.ui.eviewer.view.ViewerColumnElement;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListSelectionDialog;

/**
 * @author Ken J. Aguilar
 */
public class SetActiveColumnAction extends Action  {

   private final ElementContentProvider elementContentProvider;

   public SetActiveColumnAction(ElementContentProvider elementContentProvider) {
      super("Set Active columns");
      this.elementContentProvider = elementContentProvider;
      setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("ACTIVE_PNG"));
   }

   @Override
   public void run() {
      final IStructuredContentProvider contentProvider = new IStructuredContentProvider() {

         @Override
         public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // TODO Auto-generated method stub

         }

         @Override
         public void dispose() {
            // TODO Auto-generated method stub

         }

         @Override
         public Object[] getElements(Object inputElement) {
            return ((List<?>)inputElement).toArray();
         }
      };
      ListSelectionDialog dialog = new ListSelectionDialog(Display.getCurrent().getActiveShell(), elementContentProvider.getElementColumns(), contentProvider, new ElementLabelProvider(), "Check all columns that should be active");
      dialog.setTitle("Set Active Columns");
      LinkedList<ViewerColumn> list = new LinkedList<ViewerColumn>();
      for (ViewerColumnElement column : elementContentProvider.getElementColumns()) {
         if (column.isActive()) {
            list.add(column);
         }
      }
      dialog.setInitialElementSelections(list);
      dialog.open();
      Object[] selection = dialog.getResult();
      if (selection != null) {
         HashSet<Object> activeColumnSet = new HashSet<Object>(Arrays.asList(selection));
         for (ViewerColumnElement column : elementContentProvider.getElementColumns()) {
            column.setActive(activeColumnSet.contains(column));
         }
      }
   }


}
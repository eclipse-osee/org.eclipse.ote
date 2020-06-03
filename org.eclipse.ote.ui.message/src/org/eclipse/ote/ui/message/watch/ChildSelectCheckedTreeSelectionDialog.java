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

package org.eclipse.ote.ui.message.watch;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @author Andrew M. Finkbeiner
 */
public class ChildSelectCheckedTreeSelectionDialog extends CheckedTreeSelectionDialog {

   @Override
   public Object[] getResult() {
      Object[] objs = super.getResult();
      return objs;
   }

   private CheckboxTreeViewer viewer;

   public ChildSelectCheckedTreeSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
      super(parent, labelProvider, contentProvider);
   }

   @Override
   protected CheckboxTreeViewer createTreeViewer(Composite parent) {
      viewer = super.createTreeViewer(parent);
      viewer.addCheckStateListener(new ICheckStateListener() {
         @Override
         public void checkStateChanged(CheckStateChangedEvent event) {
            viewer.expandToLevel(event.getElement(), 1);
            viewer.setSubtreeChecked(event.getElement(), event.getChecked());
         }
      });
      return viewer;
   }
}

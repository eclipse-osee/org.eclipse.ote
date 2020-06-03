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

package org.eclipse.ote.test.manager.uut.selector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;

/**
 * @author David N. Phillips
 * @author Andy Jury
 */
public class UutSelectCustomMenu extends XViewerCustomMenu {
   private Action addAction;
   private Action duplicateAction;
   private Action deleteAction;
   private IUutItem selectedItem;
   private UutSelectionTable uutXviewer;

   @Override
   public void init(final XViewer xviewer) {
      super.init(xviewer);
      uutXviewer = (UutSelectionTable)xViewer;
      addAction = new org.eclipse.jface.action.Action("Add New") {
         @Override
         public void run() {
            String partition = "SoftwareUnit1";
            if (selectedItem != null) {
               partition = selectedItem.getPartition();
            }
            IUutItem item = uutXviewer.getContentProvider().addUutItem(partition, "");
            uutXviewer.setItemSelected(item);
         }
      };
      duplicateAction = new org.eclipse.jface.action.Action("Duplicate") {
         @Override
         public void run() {
            if (selectedItem != null) {
               IUutItem item =uutXviewer.getContentProvider().addUutItem(selectedItem.getPartition(), selectedItem.getPath());
               uutXviewer.setItemSelected(item);
            }
         }
      };
      deleteAction = new org.eclipse.jface.action.Action("Delete") {
         @Override
         public void run() {
            if (selectedItem != null) {
               ((UutSelectionTable)xViewer).getContentProvider().removeUutSelection(selectedItem);
            }
         }
      };
   }

   @Override
   protected void setupMenuForTable() {
      selectedItem = (IUutItem) ((StructuredSelection) xViewer.getSelection()).getFirstElement();
      if (selectedItem != null && selectedItem.isLeaf()) {
         duplicateAction.setEnabled(true);
      } 
      else {
         duplicateAction.setEnabled(false);
      }
      MenuManager mm = xViewer.getMenuManager();
      mm.add(addAction);
      mm.add(duplicateAction);
      mm.add(deleteAction);
   }
}

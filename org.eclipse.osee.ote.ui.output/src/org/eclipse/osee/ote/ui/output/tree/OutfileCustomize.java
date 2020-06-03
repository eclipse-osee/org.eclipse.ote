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

package org.eclipse.osee.ote.ui.output.tree;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;
import org.eclipse.osee.ote.ui.output.tree.items.IOutfileTreeItem;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OutfileCustomize extends XViewerCustomMenu {

   private Action expandAll;
   private Action collapseAll;
   private Action expandChildren;
   private Action jumpToScript;
   private IManagedForm managedForm;

   public OutfileCustomize() {
      super();

   }

   public OutfileCustomize(IManagedForm managedForm) {
      this.managedForm = managedForm;
   }

   @Override
   public void init(XViewer xviewer) {
      super.init(xviewer);
   }

   @Override
   protected void setupMenuForTable() {
      MenuManager mm = xViewer.getMenuManager();
      mm.add(new GroupMarker(XViewer.MENU_GROUP_PRE));
      mm.add(new Separator());
      mm.add(expandChildren);
      mm.add(expandAll);
      mm.add(collapseAll);
      mm.add(new Separator());
      mm.add(jumpToScript);
      mm.add(new Separator());
      mm.add(clearAllSorting);
      mm.add(copySelected);
      mm.add(this.viewTableReport);
      mm.add(new GroupMarker(XViewer.MENU_GROUP_POST));
   }

   private void reflowForm() {
      if (managedForm != null) {
         managedForm.reflow(true);
      }
   }

   @Override
   protected void setupActions() {
      super.setupActions();
      expandAll = new Action("Expand All Elements") {
         @Override
         public void run() {
            xViewer.expandAll();
            reflowForm();
         };
      };
      collapseAll = new Action("Collapse All Elements") {
         @Override
         public void run() {
            xViewer.collapseAll();
            reflowForm();
         };
      };
      expandChildren = new Action("Expand Children") {
         @Override
         public void run() {
            ((OutfileTreeXViewer) xViewer).expandChildren();
            reflowForm();
         }
      };
      jumpToScript = new Action("Jump To Script") {
         @Override
         public void run() {
            TreeSelection selection = (TreeSelection) xViewer.getSelection();
            Object obj = selection.getFirstElement();
            if (obj instanceof IOutfileTreeItem) {
               IOutfileTreeItem item = (IOutfileTreeItem) obj;
               item.run();
            }
         }
      };
   }

   public boolean doCustomizeInCurrentThread() {
      return true;
   }
}

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

package org.eclipse.osee.ote.ui.output.editors;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.osee.ote.ui.output.tree.OutfileTreeXViewer;
import org.eclipse.osee.ote.ui.output.tree.OutfileTreeXViewer.OutfileType;
import org.eclipse.osee.ote.ui.output.tree.OutfileXViewerFactory;
import org.eclipse.osee.ote.ui.output.tree.items.BaseOutfileTreeItem;
import org.eclipse.osee.ote.ui.output.tree.items.IOutfileTreeItem;
import org.eclipse.osee.ote.ui.output.tree.items.OutfileRowType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class DetailSection {
   private final String title;
   private final IOutfileTreeItem invisibleRoot = new BaseOutfileTreeItem(OutfileRowType.unknown);
   private OutfileTreeXViewer outfileTree;
   private final DetailPage detailPage;

   public DetailSection(DetailPage detailPage, String title) {
      this.title = title;
      this.detailPage = detailPage;
   }

   public void createFormContent(Composite parent) {
      parent.setLayout(new GridLayout());

      outfileTree =
            new OutfileTreeXViewer(parent, new OutfileXViewerFactory(detailPage.getManagedForm()),
                  OutfileType.Content);

      Tree tree = outfileTree.getTree();
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(false);
      
      outfileTree.setInput(invisibleRoot);
      outfileTree.refresh();
      this.detailPage.getManagedForm().reflow(true);

      outfileTree.addSelectionChangedListener(new ISelectionChangedListener() {
         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            TreeSelection sel = (TreeSelection) event.getSelection();
            detailPage.checkCurrentJumpTo((IOutfileTreeItem) sel.getFirstElement());
         }
      });

      outfileTree.addDoubleClickListener(new IDoubleClickListener() {
         @Override
         public void doubleClick(DoubleClickEvent event) {
            TreeSelection selection = (TreeSelection) event.getSelection();
            Object obj = selection.getFirstElement();
            if (obj instanceof IOutfileTreeItem) {
               IOutfileTreeItem item = (IOutfileTreeItem) obj;
               item.run();
            }
         }
      });
   }

   public void addDetailsData(IOutfileTreeItem item) {
      invisibleRoot.getChildren().add(item);
      outfileTree.setInput(item);
      detailPage.getManagedForm().reflow(true);
   }

   public boolean navigateToTreeItem(IOutfileTreeItem item) {
      long start = System.currentTimeMillis();
      if (outfileTree == null) {
         return false;
      }
      Object[] path = outfileTree.locateItem(item);
      System.out.println("Navigate to item: " + (System.currentTimeMillis() - start));
      if (path == null) {
         return false;
      }
      start = System.currentTimeMillis();
      outfileTree.getTree().setRedraw(false);
      Object[] expandPath = new Object[path.length - 1];
      System.arraycopy(path, 0, expandPath, 0, expandPath.length);
      System.out.println("doing the copy expanded to item: " + (System.currentTimeMillis() - start));
      start = System.currentTimeMillis();
      outfileTree.setExpandedElements(expandPath);
      System.out.println("set expanded to item: " + (System.currentTimeMillis() - start));
      start = System.currentTimeMillis();
      outfileTree.setSelection(new TreeSelection(new TreePath(path)), true);
      System.out.println("set selection to item: " + (System.currentTimeMillis() - start));
      start = System.currentTimeMillis();
      outfileTree.getTree().setRedraw(true);
      System.out.println("set redraw to item: " + (System.currentTimeMillis() - start));
      start = System.currentTimeMillis();

      System.out.println("set section expanded to item: " + (System.currentTimeMillis() - start));
      start = System.currentTimeMillis();

      this.detailPage.getManagedForm().reflow(true);
      System.out.println("reflow to item: " + (System.currentTimeMillis() - start));

      outfileTree.getTree().setFocus();
      
      return true;
   }

   public void refresh() {
      if (outfileTree != null) {
         outfileTree.refresh();
      }
   }

   public void clear() {
      // Intentionally Empty Block
   }

   public void addContent(StringBuilder builder) {
      builder.append("\n");
      builder.append(this.title);
      for (IOutfileTreeItem item : invisibleRoot.getChildren()) {
         recursiveAdd(item, builder, 1);
      }
   }

   private void recursiveAdd(IOutfileTreeItem item, StringBuilder sb, int level) {
      for (int i = 0; i < level; i++) {
         sb.append("\t");
      }
      sb.append(item.toString());
      sb.append("\n");
      for (IOutfileTreeItem child : item.getChildren()) {
         if (child.getType() != OutfileRowType.stacktrace) {
            recursiveAdd(child, sb, level + 1);
         }
      }
   }

   public void dispose() {
      invisibleRoot.getChildren().clear();
      outfileTree.dispose();
   }
}

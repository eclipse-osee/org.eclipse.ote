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
package org.eclipse.ote.test.manager.uut.selector;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author David N. Phillips
 * @author Andy Jury
 */
public class UutSelectionTable extends XViewer {

   public UutSelectionTable(Composite parent, int style) {
      super(parent, style, new UutSelectionViewerFactory(), true, true);
   }

   @Override
   public XViewerTextFilter getXViewerTextFilter() {
      return new XViewerTextFilter(this) {
         @Override
         public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (element instanceof UutItemPartition && isObjectExpanded(element)) {
               UutItemPartition item = (UutItemPartition) element;
               for (Object path : item.getChildren()) {
                  if (super.select(viewer, item, path)) {
                     return true;
                  }
               }
            }
            return super.select(viewer, parentElement, element);
         }
      };
   }

   protected boolean isObjectExpanded(Object element) {
      for (TreeItem item : getTree().getItems()) {
         if (item.getData() == element) {
            return  item.getExpanded();
         }
      }
      return false;
   }

   @Override
   public UutSelectionContentProvider getContentProvider() {
      return (UutSelectionContentProvider) super.getContentProvider();
   }

   @Override
   public boolean handleLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      return handleLeftClickInIconArea(treeColumn, treeItem);
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      if (treeColumn.getData().equals(UutSelectionViewerFactory.SELECTED)) {
         if (treeItem != null) {
            IUutItem item = (IUutItem) treeItem.getData();
            boolean selected = !item.isSelected();
            item.setSelected(selected);
            refresh();
            return true;
         }
      }
      return false;
   }

   @Override
   public void handleDoubleClick(TreeColumn col, TreeItem item) {
      if (item != null) {
         item.setExpanded(!item.getExpanded());
         refresh();
      }
   }

   public void setItemSelected(final Object target) {
      TreeItem[] items = getTree().getItems();
      ensureExpanded(target, items);
      selectItem(target, items);
   }

   private boolean selectItem(Object target, TreeItem[] items) {
      for (TreeItem item : items) {
         if (item.getData() == target) {
            getTree().select(item);
            getTree().showSelection();
            return true;
         }
         if (selectItem(target, item.getItems())) {
            return true;
         }
      }
      return false;
   }

   private void ensureExpanded(Object target, TreeItem[] items) {
      if (target instanceof UutItemPath) {
         UutItemPartition itemPartition = ((UutItemPath)target).getParent();
         for (TreeItem item : items) {
            if (item.getData() == itemPartition && !item.getExpanded()) {
               item.setExpanded(true);
               refresh();
               return;
            }
         }
      }
   }

}

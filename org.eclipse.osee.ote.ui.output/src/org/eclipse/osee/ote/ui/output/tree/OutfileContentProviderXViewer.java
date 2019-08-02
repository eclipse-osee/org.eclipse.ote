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
package org.eclipse.osee.ote.ui.output.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ote.ui.output.tree.items.IOutfileTreeItem;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OutfileContentProviderXViewer implements ITreeContentProvider {

   public OutfileContentProviderXViewer() {
   }

   @Override
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof IOutfileTreeItem) {
         IOutfileTreeItem outfileElement = (IOutfileTreeItem) parentElement;
         return outfileElement.getChildren().toArray();
      }
      return null;
   }

   @Override
   public Object getParent(Object element) {
      if (element instanceof IOutfileTreeItem) {
         IOutfileTreeItem outfileElement = (IOutfileTreeItem) element;
         return outfileElement.getParent();
      }
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof IOutfileTreeItem) {
         IOutfileTreeItem outfileElement = (IOutfileTreeItem) element;
         return outfileElement.getChildren().size() > 0;
      }
      return false;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   @Override
   public void dispose() {
      // Intentionally Empty Block
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // Intentionally Empty Block
   }

}

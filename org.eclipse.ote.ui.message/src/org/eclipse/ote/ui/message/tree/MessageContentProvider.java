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

package org.eclipse.ote.ui.message.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MessageContentProvider implements ITreeContentProvider {
   private Viewer viewer;
   private RootNode rootInput = null;

   @Override
   public Object[] getChildren(Object parentElement) {
      assert parentElement instanceof AbstractTreeNode;
      return ((AbstractTreeNode) parentElement).getChildren().toArray();
   }

   @Override
   public Object getParent(Object element) {
      assert element instanceof AbstractTreeNode;
      return ((AbstractTreeNode) element).getParent();
   }

   @Override
   public boolean hasChildren(Object element) {
      assert element instanceof AbstractTreeNode;
      return ((AbstractTreeNode) element).hasChildren();
   }

   @Override
   public Object[] getElements(Object inputElement) {
      if (inputElement == null) {
         return new Object[0];
      }
      return getChildren(inputElement);
   }

   @Override
   public void dispose() {
      // required to implement by IContentProvider
   }

   public void clear() {
      if (rootInput != null) {
         rootInput.removeAll();
         if (viewer != null) {
            viewer.refresh();
         }
      }
   }

   public void refresh() {
      viewer.refresh();
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      this.viewer = viewer;
      if (oldInput != null) {
         ((RootNode) oldInput).removeAll();
      }
      rootInput = (RootNode) newInput;
   }
}
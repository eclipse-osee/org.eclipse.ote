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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author David N. Phillips
 * @author Andy Jury
 */
public class UutSelectionContentProvider implements ITreeContentProvider {

   private TreeViewer viewer;
   private UutItemCollection uutCollection;

   public UutSelectionContentProvider() {
      viewer = null;
   }
   
   public void removeUutSelection(Object item) {
      uutCollection.remove(item);
      refreshViewer();
   }

   public void updatePartition(UutItemPath item, String partition) {
      uutCollection.updatePartition(item, partition);
      refreshViewer();
   }

   private void refreshViewer() {
      if (viewer != null) {
         viewer.refresh();
      }
   }
   
   @Override
   public void dispose() {
      // INTENTIONALLY EMPTY BLOCK
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      this.viewer = (TreeViewer) viewer;
      if (newInput != null) {
         this.uutCollection = (UutItemCollection) newInput;
      }
   }

   @Override
   public Object[] getElements(Object inputElement) {
      return uutCollection.getPartitions();
   }

   @Override
   public Object[] getChildren(Object element) {
      if (element instanceof UutItemCollection) {
         return ((UutItemCollection)element).getPartitions();
      }
      else if (element instanceof UutItemPartition) {
         return ((UutItemPartition) element).getChildren().toArray();
      }
      return null;
   }

   @Override
   public Object getParent(Object element) {
      if (element instanceof UutItemCollection) {
         return null;
      }
      else if (element instanceof UutItemPartition) {
         return ((UutItemPartition) element).getParent();
      }
      else if (element instanceof UutItemPath) {
         return ((UutItemPath)element).getParent();
      }
      return null;
   }
   

   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof UutItemCollection) {
         return ((UutItemCollection)element).getPartitions().length > 0;
      }
      else if (element instanceof UutItemPartition) {
         return ((UutItemPartition) element).getChildren().size() > 0;
      }
      return false;
   }

   public IUutItem addUutItem(String partition, String path) {
      IUutItem item = uutCollection.createItem(partition, path);
      refreshViewer();
      return item;
   }

   public void setCollection(UutItemCollection collection) {
      this.uutCollection = collection;
      refreshViewer();
   }

   public UutItemCollection getCollection() {
      return uutCollection;
   }

}

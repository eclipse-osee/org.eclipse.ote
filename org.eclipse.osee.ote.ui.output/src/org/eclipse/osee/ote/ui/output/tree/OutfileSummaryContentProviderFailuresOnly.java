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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ote.ui.output.tree.items.IOutfileTreeItem;
import org.eclipse.osee.ote.ui.output.tree.items.TestPointSummary;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OutfileSummaryContentProviderFailuresOnly implements ITreeContentProvider {

   public OutfileSummaryContentProviderFailuresOnly() {
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
      List<Object> retVal = new LinkedList<>();
      Object[] allElements = getChildren(inputElement);
      for (Object el : allElements) {
         if( el instanceof TestPointSummary) {
            TestPointSummary tps = (TestPointSummary) el;
            if(! tps.isPassed() )
               retVal.add(el);
         }
      }
      return retVal.toArray();
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

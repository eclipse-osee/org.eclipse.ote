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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.ote.ui.output.tree.items.IOutfileTreeItem;
import org.eclipse.osee.ote.ui.output.tree.items.TestPointSummary;
import org.eclipse.swt.graphics.Image;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OutfileSummaryLabelProvider extends XViewerLabelProvider {

   public OutfileSummaryLabelProvider(XViewer xViewer) {
      super(xViewer);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) throws OseeCoreException {
      if (columnIndex == 0 && element instanceof IOutfileTreeItem) {
         IOutfileTreeItem item = (IOutfileTreeItem) element;
         return item.getImage();
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn col, int columnIndex) throws OseeCoreException {
      if (element instanceof TestPointSummary) {
         TestPointSummary item = (TestPointSummary) element;
         switch (columnIndex) {
            case 0:
               return item.getFirstColumn();
            case 1:
               return item.getExpected();
            case 2:
               return item.getActual();
            case 3:
               return item.getElapsedTime();
            case 4: 
               return item.getRequirement();
         }
      }
      return null;
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // Intentionally Empty Block
   }

   @Override
   public void dispose() {
      // Intentionally Empty Block
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // Intentionally Empty Block
   }

}

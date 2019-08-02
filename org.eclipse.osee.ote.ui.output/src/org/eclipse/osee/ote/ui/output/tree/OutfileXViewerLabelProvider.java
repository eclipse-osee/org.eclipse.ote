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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ote.ui.output.tree.items.IOutfileTreeItem;
import org.eclipse.swt.graphics.Image;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OutfileXViewerLabelProvider extends XViewerLabelProvider {

   public OutfileXViewerLabelProvider(XViewer xViewer) {
      super(xViewer);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) {
      if (columnIndex == 0 && element instanceof IOutfileTreeItem) {
         IOutfileTreeItem item = (IOutfileTreeItem) element;
         return item.getImage();
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn col, int columnIndex) {
      if (element instanceof IOutfileTreeItem) {
         IOutfileTreeItem item = (IOutfileTreeItem) element;
         return item.getColumnText(columnIndex);
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

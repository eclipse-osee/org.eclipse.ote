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
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerStyledTextLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.ui.output.OteOutputImage;
import org.eclipse.osee.ote.ui.output.tree.items.IOutfileTreeItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OutfileXViewerStyledLabelProvider extends XViewerStyledTextLabelProvider {

   public OutfileXViewerStyledLabelProvider(XViewer xViewer) {
      super(xViewer);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) {
      if (columnIndex == 0 && element instanceof IOutfileTreeItem) {
         IOutfileTreeItem item = (IOutfileTreeItem) element;

         if (item.getChildFails() > 0) {
            return ImageManager.getImage(OteOutputImage.FAIL);
         } else if (item.getChildPasses() > 0) {
            return ImageManager.getImage(OteOutputImage.PASS);
         } else {
            return item.getImage();
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

   @Override
   public Color getBackground(Object element, XViewerColumn viewerColumn, int columnIndex) throws OseeCoreException {
      return null;
   }

   @Override
   public Font getFont(Object element, XViewerColumn viewerColumn, int columnIndex) throws OseeCoreException {
      return null;
   }

   @Override
   public Color getForeground(Object element, XViewerColumn viewerColumn, int columnIndex) throws OseeCoreException {
      return null;
   }

   @Override
   public StyledString getStyledText(Object element, XViewerColumn col, int column) throws OseeCoreException {
      if (element instanceof IOutfileTreeItem) {
         IOutfileTreeItem item = (IOutfileTreeItem) element;
         return item.getColumnStyledString(column);
      }
      return null;
   }

}

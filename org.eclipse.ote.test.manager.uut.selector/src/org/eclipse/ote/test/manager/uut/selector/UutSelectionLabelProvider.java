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

package org.eclipse.ote.test.manager.uut.selector;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.ote.test.manager.uut.selector.internal.OteTestManagerUutImage;
import org.eclipse.ote.test.manager.uut.selector.internal.UutAvailableEventHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author David N. Phillips
 * @author Andy Jury
 */
public class UutSelectionLabelProvider extends XViewerLabelProvider implements ITableFontProvider {
   private final Image checkImage;
   private final Image dotImage;
   private final Font defaultFont;
   private final Font boldFont;
   private final Font italicFont;
   private final Font boldItalicFont;

   private static final String AVAILABLE = "available";
   private static final String NOT_AVAILABLE = "notavailable";

   public UutSelectionLabelProvider(final UutSelectionTable xViewer) {
      super(xViewer);

      checkImage = OteTestManagerUutImage.loadImage(OteTestManagerUutImage.CHECK_GREEN);
      dotImage = OteTestManagerUutImage.loadImage(OteTestManagerUutImage.DOT);

      // By default the height of the rows is too small to show underscores when editing the content
      xViewer.getTree().addListener(SWT.MeasureItem, new Listener() {
         final int rowHeight = xViewer.getTree().getFont().getFontData()[0].getHeight() + 8;

         @Override
         public void handleEvent(Event event) {
            Rectangle bounds = event.getBounds();
            bounds.height = rowHeight;
            event.setBounds(bounds);
         }
      });

      FontData[] fds = xViewer.getTree().getFont().getFontData();

      defaultFont = new Font(null, fds);
      fds[0].setStyle(SWT.BOLD);
      boldFont = new Font(null, fds);
      fds[0].setStyle(SWT.ITALIC);
      italicFont = new Font(null, fds);
      fds[0].setStyle(SWT.BOLD | SWT.ITALIC);
      boldItalicFont = new Font(null, fds);

   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) {
      IUutItem item = (IUutItem) element;
      if (xCol.equals(UutSelectionViewerFactory.PARTITION)) {
         String indent = "";
         if (item.isLeaf()) {
            indent = "   ";
         }
         return indent + item.getPartition();
      }
      if (xCol.equals(UutSelectionViewerFactory.PATH)) {
         return item.getPath();
      }
      if (xCol.equals(UutSelectionViewerFactory.RATE)) {
         return item.getRate();
      }
      return "";
   }

   @Override
   public void dispose() {
      defaultFont.dispose();
      boldFont.dispose();
      italicFont.dispose();
      boldItalicFont.dispose();
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // INTENTIONALLY EMPTY BLOCK
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // INTENTIONALLY EMPTY BLOCK
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      IUutItem item = (IUutItem) element;
      if (xCol.equals(UutSelectionViewerFactory.SELECTED) && item.isSelected()) {
         if (item.isLeaf()) {
            return dotImage;
         } else {
            return checkImage;
         }
      }
      return null;
   }

   @Override
   public Color getForeground(Object element, int columnIndex) {
      IUutItem item = (IUutItem) element;
      final UutAvailableEventHandler handler = UutAvailableEventHandler.getHandler();
      if (handler != null && !handler.getAvailability(item.getPath())) {
         return getNotAvailableColor();
      }
      return getAvailableColor();
   }

   public Color getAvailableColor() {
      if (!JFaceResources.getColorRegistry().hasValueFor(AVAILABLE)) {
         JFaceResources.getColorRegistry().put(AVAILABLE, new RGB(0x0, 0x0, 0x0));
      }
      return JFaceResources.getColorRegistry().get(AVAILABLE);
   }

   public Color getNotAvailableColor() {
      if (!JFaceResources.getColorRegistry().hasValueFor(NOT_AVAILABLE)) {
         JFaceResources.getColorRegistry().put(NOT_AVAILABLE, new RGB(0xFF, 0x0, 0x0));
      }
      return JFaceResources.getColorRegistry().get(NOT_AVAILABLE);
   }

   @Override
   public Font getFont(Object element, int columnIndex) {
      IUutItem item = (IUutItem) element;
      final UutAvailableEventHandler handler = UutAvailableEventHandler.getHandler();
      if (!item.isLeaf() && handler != null && !handler.getAvailability(item.getPath())) {
         return boldItalicFont;
      } else if (handler != null && !handler.getAvailability(item.getPath())) {
         return italicFont;
      } else if (!item.isLeaf()) {
         return boldFont;
      } else {
         return defaultFont;
      }
   }
}

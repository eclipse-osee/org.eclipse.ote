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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ote.ui.message.messageXViewer.MessageXViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public class MessageWatchLabelProvider extends XViewerLabelProvider {

   public MessageWatchLabelProvider(MessageXViewer viewer) {
      super(viewer);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) {
      assert element instanceof AbstractTreeNode;
      return ((AbstractTreeNode) element).getImage(col);
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
   }

   @Override
   public void dispose() {

   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
   }

   @Override
   public String getColumnText(Object element, XViewerColumn col, int columnIndex) throws Exception {
      return ((AbstractTreeNode) element).getLabel(col);
   }
   
   @Override
   public Color getBackground(Object element, XViewerColumn col, int columnIndex) {
      return ((AbstractTreeNode) element).getBackground(col);
   }

   @Override
   public Color getForeground(Object element, XViewerColumn col, int columnIndex) {
      AbstractTreeNode node = (AbstractTreeNode) element;
      return node.isEnabled() ? null : Displays.getSystemColor(SWT.COLOR_RED);
   }
}

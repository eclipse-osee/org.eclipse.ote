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

package org.eclipse.ote.client.ui.core.widgets.xhost;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.ote.client.ui.core.TestHostItem;
import org.eclipse.ote.client.ui.core.widgets.ClientServerBundleVersionChecker;
import org.eclipse.ote.client.ui.internal.OteClientUiPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Andrew M. Finkbeiner
 */
public class HostTableLabelProvider extends XViewerLabelProvider {
   private final Image connectedImage;
   private final Color goldenRod;

   public HostTableLabelProvider(HostTable xViewerTest) {
      super(xViewerTest);
      connectedImage = OteClientUiPlugin.getImageDescriptor("OSEE-INF/images/connect.gif").createImage();
      Display display = Display.getCurrent();
      this.goldenRod = new Color(display, 255, 193, 37);
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) {
      TestHostItem item = (TestHostItem) element;
      if (xCol.equals(HostTableTestFactory.CONNECTED)) {
         return "";
      }
      if (xCol.equals(HostTableTestFactory.COMMENT_COLUMN)) {
         return item.getProperties().getName();
      }
      if (xCol.equals(HostTableTestFactory.HOST_COLUMN)) {
         return item.getProperties().getStation();
      }
      if (xCol.equals(HostTableTestFactory.TYPE_COLUMN)) {
         return item.getProperties().getType();
      }
      if (xCol.equals(HostTableTestFactory.UPDATE_COLUMN)) {
         return item.getProperties().getDateStarted().toString();
      }
      if (xCol.equals(HostTableTestFactory.USERS_COLUMN)) {
         return item.getProperties().getUserList();
      }
      if (xCol.equals(HostTableTestFactory.VERSION_COLUMN)) {
         return item.getProperties().getVersion();
      }
      return "unhandled column";
   }

   @Override
   public void dispose() {
      goldenRod.dispose();
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

   /*
    * (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider#getBackground(java.lang.Object,
    * org.eclipse.nebula.widgets.xviewer.XViewerColumn, int)
    */
   @Override
   public Color getBackground(Object element, XViewerColumn xCol, int columnIndex) {
      TestHostItem item = (TestHostItem) element;
      if (!ClientServerBundleVersionChecker.clientAndServerVersionsMatch(item)) {
         return goldenRod;
      }
      return super.getBackground(element, xCol, columnIndex);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      TestHostItem item = (TestHostItem) element;
      if (xCol.equals(HostTableTestFactory.CONNECTED) && item.isConnected()) {
         return connectedImage;
      }
      return null;
   }
}

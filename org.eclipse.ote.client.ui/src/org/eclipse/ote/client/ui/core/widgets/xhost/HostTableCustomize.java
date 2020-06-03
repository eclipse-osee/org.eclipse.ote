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

import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;
import org.eclipse.ote.client.ui.actions.DirectConnectHostServiceAction;
import org.eclipse.ote.client.ui.actions.PingHostServiceAction;
import org.eclipse.ote.client.ui.core.TestHostItem;

/**
 * @author Andrew M. Finkbeiner
 */
public class HostTableCustomize extends XViewerCustomMenu {

   @Override
   protected void setupMenuForTable() {
      this.xViewer.getMenuManager().add(new DirectConnectHostServiceAction());
      xViewer.getMenuManager().add(new Separator());
      TestHostItem item = (TestHostItem) ((StructuredSelection) xViewer.getSelection()).getFirstElement();
      if (item != null) {
         this.xViewer.getMenuManager().add(new PingHostServiceAction(item.getConnector()));
         xViewer.getMenuManager().add(new Separator());
      }
      super.setupMenuForTable();
   }
}

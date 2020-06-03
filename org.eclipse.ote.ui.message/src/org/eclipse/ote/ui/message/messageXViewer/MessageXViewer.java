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

package org.eclipse.ote.ui.message.messageXViewer;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Andrew M. Finkbeiner
 */
public class MessageXViewer extends XViewer {

   public MessageXViewer(Composite parent, int style) {
      super(parent, style, new MessageXViewerFactory());
   }

   public Menu getPopupMenu() {
      return this.getMenuManager().getMenu();
   }
}

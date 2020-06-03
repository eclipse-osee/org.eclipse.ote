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

package org.eclipse.ote.ui.mux.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ote.ui.mux.view.MuxView;
import org.eclipse.ui.PlatformUI;

public class OpenMuxViewAction extends Action {

   public OpenMuxViewAction() {
      super("Open Mux View");
   }

   @Override
   public void run() {
      try {
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(MuxView.VIEW_ID);
      } catch (Exception e) {
         MessageDialog.openError(Displays.getActiveShell(), "Error", "got an exception");
      }
   }

}

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

package org.eclipse.ote.client.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ote.client.ui.OteClientUiPlugin;
import org.eclipse.ote.client.ui.core.widgets.HostTableSelectionDialog;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author Andrew M. Finkbeiner
 */
public class HostSelectionAction extends Action implements ActionFactory.IWorkbenchAction {

   public static final String ID = "ote.tools.action.selectHostAction";

   public HostSelectionAction() {
      super("Ote Server Connect", OteClientUiPlugin.getImageDescriptor("OSEE-INF/images/connect.gif"));
      setId(ID);
   }

   @Override
   public void run() {
      try {
         HostTableSelectionDialog dialog = new HostTableSelectionDialog(Displays.getActiveShell());
         dialog.open();
      } catch (RuntimeException e) {
         MessageDialog.openError(Displays.getActiveShell(), "Error", "got an exception");
         e.printStackTrace();
      }
   }

   @Override
   public void dispose() {
      // INTENTIONALLY EMPTY BLOCK
   }

}

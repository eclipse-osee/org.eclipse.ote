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

package org.eclipse.ote.ui.eviewer.action;

import java.util.logging.Level;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ote.ui.eviewer.Constants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ken J. Aguilar
 */
public class EViewerAction extends Action {

   public EViewerAction() {
      super("Open Element Viewer");
   }

   @Override
   public void run() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         page.showView(Constants.VIEW_ID);
      } catch (PartInitException e) {
         OseeLog.log(EViewerAction.class, Level.SEVERE, "could not open Element Viewer", e);
         MessageDialog.openInformation(page.getWorkbenchWindow().getShell(), "Launch Error",
            "Couldn't launch Element Viewer.\nSee Error Log for details");
      }

   }
}

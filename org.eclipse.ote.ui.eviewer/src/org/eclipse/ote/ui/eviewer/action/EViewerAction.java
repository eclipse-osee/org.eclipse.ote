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
package org.eclipse.ote.ui.eviewer.action;

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ote.ui.eviewer.view.ElementViewer;
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
         page.showView(ElementViewer.VIEW_ID);
      } catch (PartInitException e) {
         OseeLog.log(EViewerAction.class, Level.SEVERE, "could not open Element Viewer", e);
         MessageDialog.openInformation(page.getWorkbenchWindow().getShell(), "Launch Error",
            "Couldn't launch Element Viewer.\nSee Error Log for details");
      }

   }
}

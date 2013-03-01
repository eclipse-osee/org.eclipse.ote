/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.eviewer.action;

import java.io.File;
import java.io.IOException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ken J. Aguilar
 */
public class LoadColumnsAction extends Action {

   private final ElementContentProvider elementContentProvider;

   public LoadColumnsAction(ElementContentProvider elementContentProvider) {
      super("Load Columns");
      this.elementContentProvider = elementContentProvider;
   }

   @Override
   public void run() {
      Shell shell = Displays.getActiveShell();
      FileDialog dialog = new FileDialog(shell, SWT.OPEN);
      dialog.setFilterExtensions(new String[] {"*.csv"});
      dialog.setText("Load Columns from file");
      String result = dialog.open();
      if (result != null) {
         File file = new File(result);
         try {
            elementContentProvider.loadColumnsFromFile(file);
         } catch (IOException ex) {
            MessageDialog.openError(shell, "Error", "Could not save file:\n" + file.getAbsolutePath());
         }
      }
   }

}

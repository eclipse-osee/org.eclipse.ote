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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ken J. Aguilar
 */
public final class ExportDataRowsToCsvFileAction extends Action {

   private static final String EXPORT_DATA_ROWS_TO_CSV = "Export Data Rows to CSV";
   private final ElementContentProvider elementContentProvider;

   public ExportDataRowsToCsvFileAction(ElementContentProvider elementContentProvider) {
      super(EXPORT_DATA_ROWS_TO_CSV, IAction.AS_PUSH_BUTTON);
      this.elementContentProvider = elementContentProvider;
   }

   @Override
   public void run() {
      Shell shell = Displays.getActiveShell();
      FileDialog dialog = new FileDialog(shell, SWT.SAVE);
      dialog.setFilterExtensions(new String[] {"*.csv"});
      dialog.setText(EXPORT_DATA_ROWS_TO_CSV);
      dialog.setOverwrite(true);
      String result = dialog.open();
      if (result != null) {
         elementContentProvider.toCsv(new File(result));
      }
   }

}

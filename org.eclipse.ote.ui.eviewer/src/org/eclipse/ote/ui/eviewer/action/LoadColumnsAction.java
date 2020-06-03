/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ote.ui.eviewer.Constants;
import org.eclipse.ote.ui.eviewer.view.ColumnFileParser;
import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.ote.ui.eviewer.view.MessageDialogs;
import org.eclipse.ote.ui.eviewer.view.ParseResult;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ken J. Aguilar
 */
public final class LoadColumnsAction extends Action {

   private final ElementContentProvider elementContentProvider;

   public LoadColumnsAction(ElementContentProvider elementContentProvider) {
      super("Load Columns");
      this.elementContentProvider = elementContentProvider;
   }

   @Override
   public void run() {
      Shell shell = Displays.getActiveShell();
      FileDialog dialog = new FileDialog(shell, SWT.OPEN);
      dialog.setFilterExtensions(Constants.COLUMN_FILE_EXTENSIONS);
      dialog.setFilterIndex(2);
      dialog.setText("Load Columns from file");
      String result = dialog.open();
      if (result != null) {
         File file = new File(result);
         ParseResult parseResult = ColumnFileParser.parse(file);
         switch (parseResult.getParseCode()) {
            case SUCCESS: 
               elementContentProvider.loadColumns(parseResult.getColumnEntries());                            
               break;
            case FILE_HAS_NO_VALID_COLUMNS: 
               MessageDialogs.openColumnFileEmptyOrBad(shell);
               break;
            case FILE_NOT_FOUND: 
               MessageDialogs.openColumnFileNotFound(shell);
               break;
            case FILE_IO_EXCEPTION:
               MessageDialogs.openColumnFileIoError(shell);
               break;
         }

      }
   }

}

/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.ote.ui.eviewer;

public final class Constants {
   public static final String INTERNAL_FILE_NAME = "element_viewer_column_state.columns";
   public static final String VIEW_ID = "org.eclipse.ote.ui.eviewer.view.ElementViewer";
   
   public static final String COLUMN_FILE_IO_ERROR = "Processing the column file has caused an exception to occur. See Error Log for details";
   public static final String COLUMN_FILE_NOT_FOUND = "The column file cannot be found found the system";
   public static final String COLUMN_FILE_IS_EMPTY = "The column file appears to be empty or the file format is invalid";

   public static final String[] COLUMN_FILE_EXTENSIONS = new String[] {"*.csv", "*.txt", "*.columns", "*"};
}

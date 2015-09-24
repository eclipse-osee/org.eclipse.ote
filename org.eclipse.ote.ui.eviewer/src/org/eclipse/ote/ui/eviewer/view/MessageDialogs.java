package org.eclipse.ote.ui.eviewer.view;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ote.ui.eviewer.Constants;
import org.eclipse.swt.widgets.Shell;

/**
 * dialog factory class
 * @author Ken J. Aguilar
 *
 */
public final class MessageDialogs {
   public static void openColumnFileEmptyOrBad(Shell shell) {
      MessageDialog.openWarning(shell, "Load File", Constants.COLUMN_FILE_IS_EMPTY);      
   }
   public static void openColumnFileNotFound(Shell shell) {
      MessageDialog.openError(shell, "Load File", Constants.COLUMN_FILE_NOT_FOUND);
   }
   public static void openColumnFileIoError(Shell shell) {
      MessageDialog.openError(shell, "Load File", Constants.COLUMN_FILE_IO_ERROR);
   }
   public static void openStreamError(Shell shell) {
      MessageDialog.openError(shell,
            "Stream Error",
            "Could not stream to file. See Error Log for details");
   }
   public static void saveColumnFileFail(Shell shell) {
      MessageDialog.openError(shell,
            "Save Column File",
            "Could not save columns to file. See Error Log for details");
   }
}

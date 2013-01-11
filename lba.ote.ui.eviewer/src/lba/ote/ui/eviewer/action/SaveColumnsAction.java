/*
 * Created on Dec 7, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.action;

import java.io.File;
import java.io.IOException;
import lba.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ken J. Aguilar
 */
public class SaveColumnsAction extends Action {

   private final ElementContentProvider elementContentProvider;

   public SaveColumnsAction(ElementContentProvider elementContentProvider) {
      super("Save Columns");
      this.elementContentProvider = elementContentProvider;
   }

   @Override
   public void run() {
      Shell shell = Displays.getActiveShell();
      FileDialog dialog = new FileDialog(shell, SWT.SAVE);
      dialog.setFilterExtensions(new String[] {"*.csv"});
      dialog.setText("Save Column file");
      String result = dialog.open();
      if (result != null) {
         File file = new File(result);
         try {
            elementContentProvider.saveColumnsToFile(file);
         } catch (IOException ex) {
            MessageDialog.openError(shell, "Error", "Could not save file:\n" + file.getAbsolutePath());
         }
      }
   }
}

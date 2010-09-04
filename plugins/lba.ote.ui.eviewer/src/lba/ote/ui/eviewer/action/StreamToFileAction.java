/*
 * Created on Oct 7, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.action;

import java.io.File;
import java.io.IOException;
import lba.ote.ui.eviewer.Activator;
import lba.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ken J. Aguilar
 */
public class StreamToFileAction extends Action {
   private final ElementContentProvider elementContentProvider;

   public static final String STREAMING = "isChecked?";

   public StreamToFileAction(ElementContentProvider elementContentProvider) {
      super("Stream To File", IAction.AS_CHECK_BOX);
      this.elementContentProvider = elementContentProvider;
      setImageDescriptor(Activator.getImageDescriptor("icons/stream.gif"));
   }

   @Override
   public void run() {
      Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
      if (isChecked()) {
         FileDialog dialog = new FileDialog(shell, SWT.SAVE);
         dialog.setFilterExtensions(new String[] {"*.csv"});
         dialog.setText("Save CSV file");
         String result = dialog.open();
         if (result != null) {
            File file = new File(result);
            try {
               elementContentProvider.streamToFile(file);
               firePropertyChange(STREAMING, Boolean.FALSE, Boolean.TRUE);
            } catch (IOException ex) {
               MessageDialog.openError(shell, "Error", "Could not setup streaming to file:\n" + file.getAbsolutePath());
            }
         } else {
            setChecked(false);
         }
      } else {
         try {
            elementContentProvider.streamToFile(null);
            firePropertyChange(STREAMING, Boolean.TRUE, Boolean.FALSE);

         } catch (IOException ex) {
            MessageDialog.openError(shell, "Error", "Could stop file streaming");
         }
      }
   }

}

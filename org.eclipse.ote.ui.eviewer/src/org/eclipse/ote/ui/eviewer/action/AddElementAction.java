/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.ote.ui.eviewer.action;

import java.util.logging.Level;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.message.ElementPath;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.ote.ui.message.util.MessageElementSelectionDialog;
import org.eclipse.ote.ui.message.util.MessageSelectionDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ken J. Aguilar
 */
public class AddElementAction extends Action {

   private final ElementContentProvider elementContentProvider;

   public AddElementAction(ElementContentProvider elementContentProvider) {
      super("Add Element", IAction.AS_PUSH_BUTTON);
      this.elementContentProvider = elementContentProvider;
      setImageDescriptor(Activator.getImageDescriptor("icons/add.png"));
   }

   @Override
   public void run() {
      Shell shell = Displays.getActiveShell();
      MessageSelectionDialog msgSelectionDialog = new MessageSelectionDialog(shell);
      if (msgSelectionDialog.open() == Window.OK) {
         Object[] result = msgSelectionDialog.getResult();
         String msgClassName = (String) result[0];
         try {
            MessageElementSelectionDialog msgElementSelectionDialog =
               new MessageElementSelectionDialog(shell, msgClassName, null);
            msgElementSelectionDialog.setMultipleSelection(true);
            msgElementSelectionDialog.setIgnoreCase(true);
            msgElementSelectionDialog.open();
            result = msgElementSelectionDialog.getResult();
            if (result != null) {
               for (Object r : result) {
                  Element element = (Element) r;
                  elementContentProvider.add(new ElementPath(element.getElementPath()));
               }

            }
         } catch (IllegalStateException ex) {
            MessageDialog.openError(shell, "No Dictionary", "A message libary has not been loaded yet");
         } catch (Exception ex) {
            OseeLog.log(AddElementAction.class, Level.SEVERE, "exception opening element selection dialog", ex);
            MessageDialog.openError(shell, "Exception",
               "An exception has ocurred while trying to access the message elements. See Error Log for details");
         }
      }

   }

}

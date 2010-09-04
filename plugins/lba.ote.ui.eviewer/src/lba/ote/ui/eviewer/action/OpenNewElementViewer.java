/*
 * Created on Oct 15, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.action;

import lba.ote.ui.eviewer.Activator;
import lba.ote.ui.eviewer.view.ElementViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ken J. Aguilar
 */
public class OpenNewElementViewer extends Action {

   public OpenNewElementViewer() {
      super("Open New Element Viewer", IAction.AS_PUSH_BUTTON);
      setImageDescriptor(Activator.getImageDescriptor("icons/sample.gif"));
   }

   @Override
   public void run() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         page.showView(ElementViewer.VIEW_ID, Long.toString(System.currentTimeMillis()), IWorkbenchPage.VIEW_ACTIVATE);
      } catch (PartInitException ex) {
         MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error",
            "Could not open view");
      }
   }

}

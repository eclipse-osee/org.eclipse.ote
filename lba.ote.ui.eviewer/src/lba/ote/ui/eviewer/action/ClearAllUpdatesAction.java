/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.action;

import lba.ote.ui.eviewer.Activator;
import lba.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

/**
 * @author Ken J. Aguilar
 */
public class ClearAllUpdatesAction extends Action {

   private final ElementContentProvider elementContentProvider;

   public ClearAllUpdatesAction(ElementContentProvider elementContentProvider) {
      super("Clear All Updates", IAction.AS_PUSH_BUTTON);
      setImageDescriptor(Activator.getImageDescriptor("icons/deleteAll.gif"));
      this.elementContentProvider = elementContentProvider;
   }

   @Override
   public void run() {
      elementContentProvider.clearAllUpdates();

   }

}

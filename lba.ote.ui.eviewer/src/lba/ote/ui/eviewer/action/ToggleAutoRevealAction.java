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
public class ToggleAutoRevealAction extends Action {

   private final ElementContentProvider elementContentProvider;

   public ToggleAutoRevealAction(ElementContentProvider elementContentProvider) {
      super("Auto Show Update", IAction.AS_CHECK_BOX);
      setImageDescriptor(Activator.getImageDescriptor("icons/auto_scroll.png"));
      this.elementContentProvider = elementContentProvider;
      setChecked(elementContentProvider.isAutoReveal());
   }

   @Override
   public void run() {
      elementContentProvider.setAutoReveal(isChecked());

   }

}

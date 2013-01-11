/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.ote.ui.eviewer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;

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

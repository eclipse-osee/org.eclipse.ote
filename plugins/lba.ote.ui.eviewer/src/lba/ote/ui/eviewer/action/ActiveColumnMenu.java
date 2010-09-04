/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.action;

import lba.ote.ui.eviewer.view.ElementColumn;
import lba.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;

/**
 * @author Ken J. Aguilar
 */
public class ActiveColumnMenu extends MenuManager implements IMenuListener {

   private final ElementContentProvider elementContentProvider;

   public ActiveColumnMenu(ElementContentProvider elementContentProvider) {
      super("Active column");
      setRemoveAllWhenShown(true);
      setVisible(true);
      this.elementContentProvider = elementContentProvider;
      addMenuListener(this);
   }

   @Override
   public void dispose() {
   }

   @Override
   public void menuAboutToShow(IMenuManager manager) {
      for (final ElementColumn column : elementContentProvider.getColumns()) {
         Action action = new Action(column.getName(), IAction.AS_CHECK_BOX) {
            @Override
            public void run() {
               column.setActive(isChecked());
            }

         };
         action.setChecked(column.isActive());
         manager.add(action);
      }
      manager.add(new Separator());
      manager.add(new Action("Deactivate All") {
         @Override
         public void run() {
            for (final ElementColumn column : elementContentProvider.getColumns()) {
               column.setActive(false);
            }
         }
      });
      manager.add(new Action("Activate All") {
         @Override
         public void run() {
            for (final ElementColumn column : elementContentProvider.getColumns()) {
               column.setActive(true);
            }
         }
      });
   }
}
/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.action;

import lba.ote.ui.eviewer.view.ElementColumn;
import lba.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;

/**
 * @author Ken J. Aguilar
 */
public class RemoveColumnMenu extends MenuManager implements IMenuListener {

   private final ElementContentProvider elementContentProvider;

   public RemoveColumnMenu(ElementContentProvider elementContentProvider) {
      super("Remove column");
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
         manager.add(new Action(column.getName()) {

            @Override
            public void run() {
               elementContentProvider.removeColumn(column);
            }

         });
      }
      manager.add(new Separator());
      manager.add(new Action("All") {
         @Override
         public void run() {
            elementContentProvider.removeAll();
         }
      });
   }
}

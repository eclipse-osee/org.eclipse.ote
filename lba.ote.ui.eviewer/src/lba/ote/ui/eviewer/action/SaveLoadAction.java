/*
 * Created on Dec 7, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.action;

import lba.ote.ui.eviewer.Activator;
import lba.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Ken J. Aguilar
 */
public class SaveLoadAction extends Action implements IMenuCreator {

   private Menu menu;
   private final ElementContentProvider elementContentProvider;

   public SaveLoadAction(ElementContentProvider elementContentProvider) {
      super("Save/Load");
      this.elementContentProvider = elementContentProvider;
      setImageDescriptor(Activator.getImageDescriptor("icons/save.gif"));
      setMenuCreator(this);
   }

   @Override
   public void dispose() {
      if (menu != null) {
         menu.dispose();
         menu = null;
      }
   }

   @Override
   public Menu getMenu(Control parent) {
      if (menu != null) {
         menu.dispose();
         menu = null;
      }
      menu = new Menu(parent);
      addAction(new SaveCsvFileAction(elementContentProvider));
      addSeperator();
      addAction(new SaveColumnsAction(elementContentProvider));
      addAction(new LoadColumnsAction(elementContentProvider));
      return menu;
   }

   @Override
   public Menu getMenu(Menu parent) {
      return null;
   }

   private void addAction(Action action) {
      ActionContributionItem item = new ActionContributionItem(action);
      item.fill(menu, -1);
   }

   private void addSeperator() {
      new Separator().fill(menu, -1);
   }
}

/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.eviewer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.Separator;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
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
   public void runWithEvent(Event event) {
      SaveColumnsAction action = new SaveColumnsAction(elementContentProvider);
      action.runWithEvent(event);
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
      addAction(new SaveColumnsAction(elementContentProvider));
      addAction(new LoadColumnsAction(elementContentProvider));
      addSeperator();
      addAction(new ExportDataRowsToCsvFileAction(elementContentProvider));
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

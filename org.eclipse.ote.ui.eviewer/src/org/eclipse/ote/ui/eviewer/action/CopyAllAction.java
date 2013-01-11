package org.eclipse.ote.ui.eviewer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class CopyAllAction extends Action implements IWorkbenchAction {
   private final ElementContentProvider elementContentProvider;

   private final Clipboard clipboard;

   public CopyAllAction(Display display, ElementContentProvider elementContentProvider) {
      super("Copy All");
      this.clipboard = new Clipboard(display);
      this.elementContentProvider = elementContentProvider;
   }

   @Override
   public void run() {
      elementContentProvider.toClipboard(clipboard);
   }

   @Override
   public void dispose() {
      clipboard.dispose();

   }

}

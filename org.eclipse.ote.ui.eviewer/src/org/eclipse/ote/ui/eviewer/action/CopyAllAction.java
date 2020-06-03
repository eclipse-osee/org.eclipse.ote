/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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

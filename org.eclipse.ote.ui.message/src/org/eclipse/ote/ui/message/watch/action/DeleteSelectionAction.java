/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.ote.ui.message.watch.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ote.ui.message.tree.WatchList;

/**
 * @author Ken J. Aguilar
 */
public class DeleteSelectionAction extends Action {

   private final WatchList watchList;
   private final IStructuredSelection selection;

   public DeleteSelectionAction(WatchList watchList, IStructuredSelection selection) {
      super("Delete");
      this.watchList = watchList;
      this.selection = selection;
   }

   @Override
   public void run() {
      watchList.deleteSelection(selection);
   }
}

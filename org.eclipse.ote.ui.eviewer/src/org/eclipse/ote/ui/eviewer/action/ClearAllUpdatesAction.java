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
      setImageDescriptor(Activator.getImageDescriptor("OSEE-INF/images/deleteAll.gif"));
      this.elementContentProvider = elementContentProvider;
   }

   @Override
   public void run() {
      elementContentProvider.clearAllUpdates();

   }

}

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
/**
 * @author Jonathon Fidiam
 */


package org.eclipse.ote.ui.eviewer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;

public class ShowElementsAsHexAction extends Action{

   private final ElementContentProvider elementContentProvider;

   public ShowElementsAsHexAction(ElementContentProvider elementContentProvider, boolean isChecked) {
      super("Show Elements as Hex", IAction.AS_CHECK_BOX);
      setImageDescriptor(Activator.getImageDescriptor("OSEE-INF/images/NumberLetter.png"));
      setToolTipText("Show Elements as Hex");
      this.elementContentProvider = elementContentProvider;
      this.setChecked(isChecked);
      if(isChecked){
         run();
      }
   }

   @Override
   public void run() {
      elementContentProvider.showNumbersAsHex(this.isChecked());
   }
}

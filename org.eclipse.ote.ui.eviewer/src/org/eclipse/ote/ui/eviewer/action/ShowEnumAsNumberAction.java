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
 * @author Jonathon Fidiam
 */
public class ShowEnumAsNumberAction extends Action {

   private final ElementContentProvider elementContentProvider;

   public ShowEnumAsNumberAction(ElementContentProvider elementContentProvider, boolean isChecked) {
      super("Show Enum As Number", IAction.AS_CHECK_BOX);
      setImageDescriptor(Activator.getImageDescriptor("OSEE-INF/images/NumberLetter.png"));
      setToolTipText("Show Enum As Number");
      this.elementContentProvider = elementContentProvider;
      this.setChecked(isChecked);
      if(isChecked){
         run();
      }
   }

   @Override
   public void run() {
      elementContentProvider.setEnumOutputNumber(this.isChecked());
   }

}

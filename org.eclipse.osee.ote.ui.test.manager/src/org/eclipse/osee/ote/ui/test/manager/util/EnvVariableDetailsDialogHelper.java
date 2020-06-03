/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.ui.test.manager.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

/**
 * @author Roberto E. Escobar
 */
public class EnvVariableDetailsDialogHelper implements Runnable {

   private final String nameOfVariableToSet;
   private int result;
   private String selection;

   public EnvVariableDetailsDialogHelper(String nameOfVariableToSet, String oldValue) {
      this.nameOfVariableToSet = nameOfVariableToSet;
      this.selection = oldValue;
   }

   public int getResult() {
      return result;
   }

   public String getSelection() {
      return selection;
   }

   @Override
   public void run() {
      EnvVariableDetailsDialog dlg =
         new EnvVariableDetailsDialog(null, "Edit " + nameOfVariableToSet, null, nameOfVariableToSet + " Value:",
            MessageDialog.NONE, new String[] {"OK", "Cancel"}, 0, selection);

      result = dlg.open();

      if (result == Window.OK) {
         if (dlg.isValid()) {
            String info = dlg.getSelection();
            if (info != null) {
               selection = info;
            } else {
               selection = "";
            }
         }
      }
   }
}

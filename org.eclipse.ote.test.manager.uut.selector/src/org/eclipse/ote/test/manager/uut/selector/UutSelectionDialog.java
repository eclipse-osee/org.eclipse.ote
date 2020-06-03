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

package org.eclipse.ote.test.manager.uut.selector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;

/**
 * @author David N. Phillips
 * @author Andy Jury
 */
public class UutSelectionDialog extends Dialog {

   private UutSelectionComposite uutSelectionComposite;
   private boolean noDefaults;
   
   public UutSelectionDialog() {
      super(Displays.getActiveShell());
      uutSelectionComposite = null;
      noDefaults = false;
   }
   
   public UutSelectionComposite getUutSelectionComposite() {
      return uutSelectionComposite;
   }
   
   public void setNoDefaults(boolean noDefaults) {
      this.noDefaults = noDefaults;
      if (uutSelectionComposite != null) {
         uutSelectionComposite.setNoDefaults(noDefaults);
      }
   }

   @Override
   protected Control createDialogArea(Composite parent){
      uutSelectionComposite = new UutSelectionComposite(parent, SWT.NONE);
      uutSelectionComposite.setNoDefaults(noDefaults);
      return uutSelectionComposite;
   }
   
   @Override
   protected boolean isResizable() {
      return true;
   }
   
   @Override
   protected  Point  getInitialSize() {
      return new Point(900, 500);
   }

   @Override
   protected void okPressed() {
      String errorMessage = uutSelectionComposite.checkErrorConditions();
      if (errorMessage.isEmpty()) {
         super.okPressed();
      }
      else {
         MessageBox messageDialog = new MessageBox(this.getShell());
         messageDialog.setMessage("Error:\n"+errorMessage);
         messageDialog.setText("Notice");
         messageDialog.open();
      }
   }
}

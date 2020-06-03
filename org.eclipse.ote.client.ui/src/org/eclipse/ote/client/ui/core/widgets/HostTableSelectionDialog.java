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

package org.eclipse.ote.client.ui.core.widgets;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ken J. Aguilar
 */
public class HostTableSelectionDialog extends TrayDialog {

   private HostSelectionTable hostTable;

   public HostTableSelectionDialog(Shell shell) {
      super(shell);
   }

   @Override
   protected void configureShell(Shell newShell) {
      super.configureShell(newShell);
      newShell.setText("Select Host");
      newShell.setSize(625, 400);
   }

   @Override
   protected void createButtonsForButtonBar(Composite parent) {
      // create OK button
      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      Composite comp = (Composite) super.createDialogArea(parent);
      initializeDialogUnits(comp);

      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.widthHint = 600;
      gd.heightHint = 300;
      this.hostTable = new HostSelectionTable(comp, SWT.NONE);
      hostTable.getTable();
      return comp;
   }

   @Override
   protected Layout getLayout() {
      return super.getLayout();
   }
   
   /* (non-Javadoc)
    * @see org.eclipse.jface.dialogs.Dialog#close()
    */
   @Override
   public boolean close() {
      if( hostTable != null ) {
         hostTable.dispose();
      }
      return super.close();
   }

}

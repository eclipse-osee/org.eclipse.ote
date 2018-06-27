/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.test.manager.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Roberto E. Escobar
 */
public class EnvVariableDialog extends MessageDialog {

   private Button cancelButton;
   private final String dialogMessage;

   private Button okButton;

   private String selection;
   private boolean selectionOk;
   private StyledText textArea;

   public EnvVariableDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, null, dialogImageType, dialogButtonLabels, defaultIndex);
      this.dialogMessage = dialogMessage;
      this.selectionOk = false;
   }

   public String getSelection() {
      return selection;
   }

   public boolean isValid() {
      return selectionOk;
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control c = super.createButtonBar(parent);
      okButton = getButton(0);
      cancelButton = getButton(1);

      okButton.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         @Override
         public void widgetSelected(SelectionEvent e) {
            selectionOk = true;

         }
      });

      cancelButton.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         @Override
         public void widgetSelected(SelectionEvent e) {
            selectionOk = false;
         }
      });
      return c;
   }

   @Override
   protected Control createCustomArea(Composite parent) {
      super.createCustomArea(parent);

      GridData gd = new GridData(SWT.FILL);
      // gd.minimumWidth = 200;
      gd.grabExcessHorizontalSpace = true;

      Composite addView = new Composite(parent, SWT.NONE);
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      addView.setLayout(gridLayout);
      addView.setLayoutData(gd);

      new Label(addView, SWT.NONE).setText(dialogMessage);

      GridData gd1 = new GridData(SWT.FILL);
      gd1.minimumWidth = 200;
      gd1.grabExcessHorizontalSpace = true;

      textArea = new StyledText(addView, SWT.SINGLE | SWT.BORDER);
      textArea.setLayoutData(gd1);
      textArea.addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            selection = textArea.getText();
         }
      });

      return parent;
   }

}

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

package org.eclipse.ote.client.ui.actions;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Andrew M. Finkbeiner
 */
public class ServerConnectDialog extends TitleAreaDialog {

   private Text serverURIText;
   private String serverURI;

   public ServerConnectDialog(Shell parentShell) {
      super(parentShell);
   }

   @Override
   public void create() {
      super.create();
      // Set the title
      setTitle("OTE Server Connect");
      // Set the message
      setMessage("Connect to OTE Server.", 
            IMessageProvider.INFORMATION);

   }

   @Override
   protected Control createDialogArea(Composite parent) {
      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      parent.setLayout(layout);

      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = GridData.FILL;

      Label label1 = new Label(parent, SWT.NONE);
      label1.setText("SERVER ACTIVEMQ URI");

      serverURIText = new Text(parent, SWT.BORDER);
      serverURIText.setLayoutData(gridData);

      return parent;
   }

   @Override
   protected void createButtonsForButtonBar(Composite parent) {
      GridData gridData = new GridData();
      gridData.verticalAlignment = GridData.FILL;
      gridData.horizontalSpan = 3;
      gridData.grabExcessHorizontalSpace = true;
      gridData.grabExcessVerticalSpace = true;
      gridData.horizontalAlignment = SWT.CENTER;

      parent.setLayoutData(gridData);
      // Create Add button
      // Own method as we need to overview the SelectionAdapter
      createOkButton(parent, OK, "Connect", true);

      Button cancelButton =  createButton(parent, CANCEL, "Cancel", false);
      cancelButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            setReturnCode(CANCEL);
            close();
         }
      });
   }

   protected Button createOkButton(Composite parent, int id, 
         String label,
         boolean defaultButton) {
      // increment the number of columns in the button bar
      ((GridLayout) parent.getLayout()).numColumns++;
      Button button = new Button(parent, SWT.PUSH);
      button.setText(label);
      button.setFont(JFaceResources.getDialogFont());
      button.setData(new Integer(id));
      button.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            if (isValidInput()) {
               okPressed();
            }
         }
      });
      if (defaultButton) {
         Shell shell = parent.getShell();
         if (shell != null) {
            shell.setDefaultButton(button);
         }
      }
      setButtonLayoutData(button);
      return button;
   }

   private boolean isValidInput() {
      boolean valid = true;
      if (serverURIText.getText().length() == 0) {
         setErrorMessage("Please input something");
         valid = false;
      }
      return valid;
   }

   @Override
   protected boolean isResizable() {
      return true;
   }

   // Coyy textFields because the UI gets disposed
   // and the Text Fields are not accessible any more.
   private void saveInput() {
      serverURI = serverURIText.getText();
   }

   @Override
   protected void okPressed() {
      saveInput();
      super.okPressed();
   }

   public String getServerURI() {
      return serverURI.trim();
   }

} 
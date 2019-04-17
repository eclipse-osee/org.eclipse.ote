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
package org.eclipse.ote.client.ui.core.widgets;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Michael P. Masterson
 */
public class OpenOrOverwriteDialog extends Dialog {
   private boolean result;
   private String folderPath;

   /**
    * @param parent
    * @param folderPath 
    */
   public OpenOrOverwriteDialog(Shell parent, String folderPath) {
      super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
      this.folderPath = folderPath;
   }
   
   public boolean open() {
      Shell parent = getParent();
      Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
      shell.setText(getText());
      createContents(shell);
      shell.pack();
      shell.open();
      Display display = parent.getDisplay();
      while (!shell.isDisposed()) {
          if (!display.readAndDispatch()) display.sleep();
      }
      return result;
   }

   /**
    * @param parent
    */
   private void createContents(final Shell parent) {
      parent.setLayout(new GridLayout(2, true));

      // Show the message
      Label label = new Label(parent, SWT.CENTER);
      GridDataFactory.fillDefaults().span(2, 1).applyTo(label);
      label.setText(String.format("Folder %s already exists.\n\n"
                                + "Do you want to open the current folder or overwrite it?", this.folderPath));
      
      Button openBtn = new Button(parent, SWT.PUSH);
      openBtn.setText("Open");
      GridDataFactory.fillDefaults().grab(false, false).applyTo(openBtn);
      openBtn.addSelectionListener(new SelectionAdapter() {
         /* (non-Javadoc)
          * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
          */
         @Override
         public void widgetSelected(SelectionEvent e) {
            result = true;
            parent.close();
         }
      });
      
      Button overwriteBtn = new Button(parent, SWT.PUSH);
      overwriteBtn.setText("Overwrite");
      GridDataFactory.fillDefaults().grab(false, false).applyTo(overwriteBtn);
      overwriteBtn.addSelectionListener(new SelectionAdapter() {
         /* (non-Javadoc)
          * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
          */
         @Override
         public void widgetSelected(SelectionEvent e) {
            result = false;
            parent.close();
         }
      });
         
   }
   
   public static void main(String[] args) {
      Display display = Display.getDefault();
      final Shell shell = new Shell (display);
      shell.setText ("Shell");
      shell.setLayout (new GridLayout(1, false));
      OpenOrOverwriteDialog dialog = new OpenOrOverwriteDialog(shell, "Some\\awesome\\path");
      System.out.println(dialog.open());
      display.dispose ();
   }
   

}

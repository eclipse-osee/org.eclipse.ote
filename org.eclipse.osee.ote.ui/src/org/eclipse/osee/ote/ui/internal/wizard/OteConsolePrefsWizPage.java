/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.internal.wizard;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ote.ui.internal.prefs.OteConsolePreferences;
import org.eclipse.osee.ote.ui.internal.prefs.OteConsolePrefsUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michael P. Masterson
 */
public class OteConsolePrefsWizPage extends WizardPage {

   private Text bufferText;
   private Label errorLabel;
   private Button noLimitCheckbox;

   private int bufferLimit;
   private boolean noLimitSelection;
   
   /**
    * @param pageName
    */
   public OteConsolePrefsWizPage() {
      super("OTE Console Preferences Wizard");
      setTitle("OTE Console Options");
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
    */
   @Override
   public void createControl(Composite parent) {
      Composite main = new Composite(parent, SWT.NONE);
      GridLayoutFactory.fillDefaults().numColumns(1).applyTo(main);
      
      addBufferLimit(main);
      addErrorLabel(main);
      determinePageComplete();
      
      setControl(main);
   }

   private void addErrorLabel(Composite parent) {
      this.errorLabel = new Label(parent, SWT.NONE);
      GridDataFactory.fillDefaults().grab(true, true).applyTo(errorLabel);
      errorLabel.setVisible(false);
   }

   /**
    * 
    */
   private void determinePageComplete() {
      boolean allIsWell = false;
      String bufferLimitStr = bufferText.getText();
      if(bufferLimitStr != null && bufferLimitStr.length() > 0 ) {
         try {
            Integer.parseInt(bufferLimitStr);
            allIsWell = true;
         } catch (NumberFormatException ex) {
            errorLabel.setText("Buffer limit must be a number between 0 and " + Integer.MAX_VALUE);
            allIsWell = false;
         }
      }

      errorLabel.setVisible(!allIsWell);
      setPageComplete(allIsWell);
   }

   /**
    * @param parent
    */
   private void addBufferLimit(Composite parent) {
      Group comp = new Group(parent, SWT.NONE);
      GridDataFactory.fillDefaults().grab(false, false).applyTo(comp);
      GridLayoutFactory.fillDefaults().numColumns(2).applyTo(comp);
      comp.setText("Select Console Buffer Limit (Bytes)");

      bufferText = new Text(comp, SWT.BORDER);
      GridDataFactory.fillDefaults().grab(true, true).applyTo(bufferText);
      
      String defaultText = OteConsolePrefsUtil.getString(OteConsolePreferences.BUFFER_LIMIT);
      bufferText.setText(defaultText);
      
      noLimitCheckbox = new Button(comp, SWT.CHECK);
      noLimitCheckbox.setText("No Limit");
      boolean defaultNoLimit = OteConsolePrefsUtil.getBoolean(OteConsolePreferences.NO_BUFFER_LIMIT);
      noLimitCheckbox.setSelection(defaultNoLimit);


      bufferText.setEnabled(!defaultNoLimit);
      
      noLimitCheckbox.addSelectionListener(new SelectionAdapter() {
         /* (non-Javadoc)
          * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
          */
         @Override
         public void widgetSelected(SelectionEvent e) {
            boolean isNoLimit = noLimitCheckbox.getSelection();
            bufferText.setEnabled(!isNoLimit);
            determinePageComplete();
            savePreferences();
         }
      });
      
      bufferText.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent e) {
            determinePageComplete(); 
         }
      });
      
      bufferText.addFocusListener(new FocusListener() {
         
         @Override
         public void focusLost(FocusEvent e) {
            determinePageComplete(); 
            savePreferences();
         }
         
         @Override
         public void focusGained(FocusEvent e) {
         }
      });
   }

   /**
    * 
    */
   protected void savePreferences() {
      if(isPageComplete() && !bufferText.isDisposed()) {
         saveNoLimitSelection();
         saveBufferLimit();
         OteConsolePrefsUtil.setInt(OteConsolePreferences.BUFFER_LIMIT, getBufferLimit());
         OteConsolePrefsUtil.setBoolean(OteConsolePreferences.NO_BUFFER_LIMIT, getNoLimitSelection());
      }
   }
   
   private void saveNoLimitSelection() {
      this.noLimitSelection = noLimitCheckbox.getSelection();
   }

   public boolean getNoLimitSelection() {
      return noLimitSelection;
   }

   private void saveBufferLimit() {
      this.bufferLimit = Integer.parseInt(bufferText.getText());
   }
   
   public int getBufferLimit() {
      return bufferLimit;
   }

}

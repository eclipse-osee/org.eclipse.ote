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

package org.eclipse.ote.client.ui.core;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.IShellCloseEvent;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
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
 * @author Andrew M. Finkbeiner
 */
public class EntryDialog extends MessageDialog {

   private StyledText text;
   private Composite areaComposite;
   private String entryText = "";
   private NumberFormat numberFormat;
   private String errorString = "";
   private Button ok;
   private Label errorLabel;

   private final List<IShellCloseEvent> closeEventListeners = new ArrayList<>();

   public EntryDialog(String dialogTitle, String dialogMessage) {
      super(Displays.getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.QUESTION, new String[] {
         "OK",
         "Cancel"}, 0);
   }

   public EntryDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
         defaultIndex);
   }

   private final ModifyListener textModifyListener = new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
         handleModified();
      }
   };

   private final MouseMoveListener compListener = new MouseMoveListener() {
      @Override
      public void mouseMove(MouseEvent e) {
         setInitialButtonState();
      }
   };

   @Override
   protected Control createCustomArea(Composite parent) {
      areaComposite = new Composite(parent, SWT.NONE);
      areaComposite.setLayout(new GridLayout(2, false));
      areaComposite.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
      areaComposite.addMouseMoveListener(compListener);

      createErrorLabel(areaComposite);
      createTextBox(areaComposite);

      createExtendedArea(areaComposite);
      areaComposite.layout();
      parent.layout();
      return areaComposite;
   }

   private void createErrorLabel(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(3, false));
      GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
      gd1.horizontalSpan = 2;
      composite.setLayoutData(gd1);

      errorLabel = new Label(composite, SWT.NONE);
      errorLabel.setSize(errorLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      errorLabel.setText("");
      GridData gd = new GridData();
      gd.horizontalSpan = 3;
      errorLabel.setLayoutData(gd);
   }

   private void createTextBox(Composite parent) {
      text = new StyledText(parent, SWT.BORDER);
      text.setLayout(new GridLayout());
      text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      if (Strings.isValid(entryText)) {
         text.setText(entryText);
      }
      text.addModifyListener(textModifyListener);
      text.setFocus();
   }

   @Override
   protected boolean isResizable() {
      return true;
   }

   protected void createExtendedArea(Composite parent) {
      // INTENTIONALLY EMPTY BLOCK
   }

   public void setInitialButtonState() {
      if (ok == null) {
         ok = getButton(0);
         handleModified();
      }
      areaComposite.removeMouseMoveListener(compListener);
   }

   public void handleModified() {
      if (text != null) {
         entryText = text.getText();
         if (!isEntryValid()) {
            getButton(getDefaultButtonIndex()).setEnabled(false);
            errorLabel.setText(errorString);
            errorLabel.update();
            areaComposite.layout();
         } else {
            getButton(getDefaultButtonIndex()).setEnabled(true);
            errorLabel.setText("");
            errorLabel.update();
            areaComposite.layout();
         }
      }
   }

   public String getEntry() {
      return entryText;
   }

   public void setEntry(String entry) {
      if (text != null) {
         text.setText(entry);
      }
      this.entryText = entry;
   }

   /**
    * override this method to make own checks on entry this will be called with every keystroke
    *
    * @return true if entry is valid
    */
   public boolean isEntryValid() {
      if (numberFormat == null) {
         return true;
      }

      try {
         numberFormat.parse(text.getText());
      } catch (ParseException ex) {
         return false;
      }

      return true;
   }

   public void setValidationErrorString(String errorString) {
      this.errorString = errorString;
   }

   public void setNumberFormat(NumberFormat numberFormat) {
      this.numberFormat = numberFormat;
   }

   /**
    * Calling will enable dialog to loose focus
    */
   public void setModeless() {
      setShellStyle(SWT.DIALOG_TRIM | SWT.MODELESS);
      setBlockOnOpen(false);
   }

   public void setSelectionListener(SelectionListener listener) {
      for (int i = 0; i < getButtonLabels().length; i++) {
         Button button = getButton(i);
         button.addSelectionListener(listener);
      }
   }

   @Override
   protected void handleShellCloseEvent() {
      super.handleShellCloseEvent();
      for (IShellCloseEvent event : closeEventListeners) {
         event.onClose();
      }
   }

   public void addShellCloseEventListeners(IShellCloseEvent event) {
      closeEventListeners.add(event);
   }

}

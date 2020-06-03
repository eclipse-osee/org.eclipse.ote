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

package org.eclipse.ote.test.manager.panels;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Roberto E. Escobar
 */
public class ServerOutputPanel extends Composite {
   private static final String LABEL_TEXT = "Enter File Path on Server: ";
   private static final String TOOLTIP_TEXT =
      "Enter a full unix path and filename to redirect execution output to a file.";

   private enum SelectionsEnum {
      None,
      File_On_Server,
      Console;

      public static SelectionsEnum fromString(String value) {
         SelectionsEnum toReturn = SelectionsEnum.None;
         if (Strings.isValid(value) != false) {
            for (SelectionsEnum formatType : SelectionsEnum.values()) {
               if (formatType.name().equalsIgnoreCase(value)) {
                  toReturn = formatType;
                  break;
               }
            }
         }
         return toReturn;
      }
   }

   private Map<SelectionsEnum, Button> buttonMap;
   private SelectionsEnum lastSelected;
   private StyledText textField;
   private Composite textAreaComposite;

   public ServerOutputPanel(Composite parent, int style) {
      super(parent, style);
      GridLayout gl = new GridLayout();
      gl.marginHeight = 0;
      gl.marginWidth = 0;
      this.setLayout(gl);
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      createControl(this);
   }

   private void createControl(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout gl = new GridLayout();
      gl.marginHeight = 0;
      gl.marginWidth = 0;
      composite.setLayout(gl);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      createButtonBar(composite);
      createTextArea(composite);
   }

   private void createButtonBar(Composite parent) {
      SelectionsEnum[] selects = SelectionsEnum.values();
      this.buttonMap = new HashMap<>();

      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout gl = new GridLayout(selects.length, false);
      gl.marginHeight = 0;
      gl.marginWidth = 0;
      composite.setLayout(gl);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      for (int index = 0; index < selects.length; index++) {
         SelectionsEnum selectionType = selects[index];

         Button button = new Button(composite, SWT.RADIO);
         button.setData(selectionType);
         button.setText(selectionType.name().replaceAll("_", " "));
         boolean isFirst = index == 0;
         button.setSelection(isFirst);
         if (isFirst != false) {
            lastSelected = selectionType;
         }
         button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               Object object = e.getSource();
               if (object instanceof Button) {
                  setSelected((Button) object);
               }
            }

         });
         buttonMap.put(selectionType, button);
      }
   }

   private void createTextArea(Composite parent) {
      textAreaComposite = new Composite(parent, SWT.NONE);
      textAreaComposite.setLayout(new GridLayout(2, false));
      textAreaComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Label labelField = new Label(textAreaComposite, SWT.NONE);
      labelField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
      labelField.setText(LABEL_TEXT);
      labelField.setToolTipText(TOOLTIP_TEXT);

      textField = new StyledText(textAreaComposite, SWT.BORDER);
      textField.setToolTipText(TOOLTIP_TEXT);
      textField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      textAreaComposite.setVisible(false);
   }

   private void setSelected(Button button) {
      if (button.getSelection() != false) {
         lastSelected = (SelectionsEnum) button.getData();

         boolean textAreaEnabled = isFileOnServerAllowed() != false;
         textAreaComposite.setVisible(textAreaEnabled);
         this.layout();
      }
   }

   public String getSelected() {
      return lastSelected.toString();
   }

   private boolean isFileOnServerAllowed() {
      return lastSelected.equals(SelectionsEnum.File_On_Server);
   }

   public boolean areSettingsValid() {
      boolean toReturn = true;
      if (isFileOnServerAllowed() != false) {
         toReturn = Strings.isValid(getFilePath());
      }
      return toReturn;
   }

   public String getErrorMessage() {
      return areSettingsValid() != true ? "Console file path cannot be empty." : "";
   }

   public void setFilePath(String value) {
      textField.setText(value);
   }

   public String getFilePath() {
      String value = textField.getText();
      return value != null ? value : "";
   }

   public void setSelected(String value) {
      SelectionsEnum selected = SelectionsEnum.fromString(value);
      this.lastSelected = selected;
      for (SelectionsEnum keys : buttonMap.keySet()) {
         Button button = buttonMap.get(keys);
         button.setSelection(keys.equals(selected));
      }
   }
}

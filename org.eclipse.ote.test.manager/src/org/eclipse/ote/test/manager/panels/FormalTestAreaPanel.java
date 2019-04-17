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
package org.eclipse.ote.test.manager.panels;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Roberto E. Escobar
 */
public class FormalTestAreaPanel extends Composite {
   private static final String EMPTY_STRING = "";
   private static final String[] EMPTY_STRING_ARRAY = new String[0];

   private enum FormatTestTypes {
      Development,
      DryRun,
      Demo;

      public static FormatTestTypes fromString(String value) {
         FormatTestTypes toReturn = FormatTestTypes.Development;
         if (Strings.isValid(value) != false) {
            for (FormatTestTypes formatType : FormatTestTypes.values()) {
               if (formatType.name().equalsIgnoreCase(value)) {
                  toReturn = formatType;
                  break;
               }
            }
         }
         return toReturn;
      }
   }

   private Composite stackedComposite;
   private StackLayout stackLayout;
   private Composite defaultComposite;
   private TestRunEntryPanel testRunEntryPanel;
   private Map<FormatTestTypes, Button> buttonMap;
   private FormatTestTypes lastSelected;

   public FormalTestAreaPanel(Composite parent, int style) {
      super(parent, style);

      this.setLayout(new GridLayout(2, false));
      this.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
      createControl(this);
   }

   private void createControl(Composite parent) {
      createButtonBar(parent);
      createStackedArea(parent);
   }

   private void createStackedArea(Composite parent) {
      stackedComposite = new Composite(parent, SWT.NONE);
      stackLayout = new StackLayout();
      stackLayout.marginHeight = 0;
      stackLayout.marginWidth = 0;
      stackedComposite.setLayout(stackLayout);
      stackedComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      createDefaultComposite(stackedComposite);
      createEntryForm(stackedComposite);
   }

   private void createEntryForm(Composite parent) {
      this.testRunEntryPanel = new TestRunEntryPanel(parent, SWT.BORDER);
   }

   private void createDefaultComposite(Composite parent) {
      defaultComposite = new Composite(parent, SWT.NONE);
      GridLayout gL = new GridLayout();
      gL.marginHeight = 0;
      gL.marginWidth = 0;
      defaultComposite.setLayout(gL);
      defaultComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
   }

   private void createButtonBar(Composite parent) {
      this.buttonMap = new HashMap<>();
      FormatTestTypes[] formats = FormatTestTypes.values();

      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

      for (int index = 0; index < formats.length; index++) {
         FormatTestTypes testType = formats[index];
         Button button = new Button(composite, SWT.RADIO);
         button.setText(testType.name());

         boolean isFirst = index == 0;
         if (isFirst != false) {
            this.lastSelected = testType;
         }
         button.setSelection(isFirst);
         button.setData(testType);
         button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               Object object = e.getSource();
               if (object instanceof Button) {
                  updateDisplay((Button) object);
               }
            }
         });
         this.buttonMap.put(testType, button);
      }
   }

   private void updateDisplay(Button button) {
      if (button.getSelection() != false) {
         FormatTestTypes testType = (FormatTestTypes) button.getData();
         this.lastSelected = testType;
         setDisplay(testType);
      }
   }

   private Control getControl(FormatTestTypes testType) {
      Control control = defaultComposite;
      switch (testType) {
         case Demo:
         case DryRun:
            control = testRunEntryPanel;
            break;
         default:
            break;
      }
      return control;
   }

   private void setDisplay(final FormatTestTypes testType) {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            Control control = getControl(testType);
            stackLayout.topControl = control;
            stackedComposite.layout();
            stackedComposite.getParent().layout();
            getParent().layout();
            getParent().getParent().layout();
         }
      });
   }

   public String getFormalTestType() {
      return lastSelected.name();
   }

   public void setFormalTestType(String value) {
      FormatTestTypes formatTestTypes = FormatTestTypes.fromString(value);
      for (FormatTestTypes keys : buttonMap.keySet()) {
         Button button = buttonMap.get(keys);
         button.setSelection(keys.equals(formatTestTypes));
         updateDisplay(button);
      }
   }

   public boolean isDemoSelected() {
      return lastSelected.equals(FormatTestTypes.Development) != true;
   }

   public String getDemoNotes() {
      return isDemoSelected() != false ? testRunEntryPanel.getNotes() : EMPTY_STRING;
   }

   public String getDemoBuildId() {
      return isDemoSelected() != false ? testRunEntryPanel.getBuildId() : EMPTY_STRING;
   }

   public String[] getDemoWitnessNames() {
      return isDemoSelected() != false ? testRunEntryPanel.getWitnessNames() : EMPTY_STRING_ARRAY;
   }

   public String[] getDemoExecutedByNames() {
      return isDemoSelected() != false ? testRunEntryPanel.getRunnerNames() : EMPTY_STRING_ARRAY;
   }

   public void setDemoNotes(String value) {
      this.testRunEntryPanel.setNotes(value);
   }

   public void setDemoBuildId(String value) {
      this.testRunEntryPanel.setBuildId(value);
   }

   public void setDemoWitnessNames(String[] value) {
      this.testRunEntryPanel.setWitnessNames(value);
   }

   public void setDemoExecutedByNames(String[] value) {
      this.testRunEntryPanel.setExecutedByNames(value);
   }
}

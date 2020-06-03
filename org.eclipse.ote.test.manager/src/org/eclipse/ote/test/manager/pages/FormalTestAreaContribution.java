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

package org.eclipse.ote.test.manager.pages;

import org.eclipse.ote.test.manager.panels.FormalTestAreaPanel;

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.widgets.IPropertyStoreBasedControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

/**
 * @author Roberto E. Escobar
 */
public class FormalTestAreaContribution implements IPropertyStoreBasedControl {

   private static final String GROUP_TEXT = "Formal Testing Level";
   private FormalTestAreaPanel panel;

   @Override
   public Control createControl(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      GridLayout gL = new GridLayout();
      gL.marginHeight = 0;
      gL.marginWidth = 0;

      group.setLayout(gL);
      group.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
      group.setText(GROUP_TEXT);

      this.panel = new FormalTestAreaPanel(group, SWT.NONE);
      return group;
   }

   @Override
   public void load(IPropertyStore propertyStore) {
      String formalTestType = propertyStore.get(StorageKeys.FORMAL_TEST_TYPE);

      String notes = propertyStore.get(StorageKeys.DEMONSTRATION_NOTES);
      String buildId = propertyStore.get(StorageKeys.DEMONSTRATION_BUILD);
      String[] witnesses = propertyStore.getArray(StorageKeys.DEMONSTRATION_WITNESSES);
      String[] executedBy = propertyStore.getArray(StorageKeys.DEMONSTRATION_EXECUTED_BY);

      this.panel.setFormalTestType(formalTestType);
      this.panel.setDemoNotes(notes);
      this.panel.setDemoBuildId(buildId);
      this.panel.setDemoWitnessNames(witnesses);
      this.panel.setDemoExecutedByNames(executedBy);

   }

   @Override
   public void save(IPropertyStore propertyStore) {
      propertyStore.put(StorageKeys.FORMAL_TEST_TYPE, this.panel.getFormalTestType());

      // Save Demo Info
      propertyStore.put(StorageKeys.DEMONSTRATION_NOTES, this.panel.getDemoNotes());
      propertyStore.put(StorageKeys.DEMONSTRATION_BUILD, this.panel.getDemoBuildId());
      propertyStore.put(StorageKeys.DEMONSTRATION_WITNESSES, this.panel.getDemoWitnessNames());
      propertyStore.put(StorageKeys.DEMONSTRATION_EXECUTED_BY, this.panel.getDemoExecutedByNames());
   }

   @Override
   public boolean areSettingsValid() {
      boolean toReturn = true;
      if (this.panel.isDemoSelected() != false) {
         toReturn &= Strings.isValid(this.panel.getDemoBuildId());
         toReturn &= this.panel.getDemoWitnessNames().length > 0;
         toReturn &= this.panel.getDemoExecutedByNames().length > 0;
      }
      return toReturn;
   }

   @Override
   public String getErrorMessage() {
      StringBuilder builder = new StringBuilder();
      if (areSettingsValid() != true) {
         builder.append("Formal Testing [DEMO]: Please enter - ");
         boolean valueEntered = false;
         if (Strings.isValid(this.panel.getDemoBuildId()) != true) {
            builder.append("build id");
            valueEntered = true;
         }
         if (this.panel.getDemoWitnessNames().length == 0) {
            if (valueEntered) {
               builder.append(", ");
            }
            builder.append("at least one witness");
            valueEntered = true;
         }
         if (this.panel.getDemoExecutedByNames().length == 0) {
            if (valueEntered) {
               builder.append(", ");
            }
            builder.append("at least one executed by name");
         }
      }
      return builder.toString();
   }

   @Override
   public int getPriority() {
      return 10;
   }
}

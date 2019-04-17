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
package org.eclipse.ote.test.manager.pages;

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.ui.plugin.widgets.IPropertyStoreBasedControl;
import org.eclipse.ote.test.manager.panels.ServerOutputPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

/**
 * @author Roberto E. Escobar
 * @author Andy Jury
 */
public class UutConsoleOutput implements IPropertyStoreBasedControl {

   private ServerOutputPanel consoleOutputPanel;

   @Override
   public boolean areSettingsValid() {
      return consoleOutputPanel.areSettingsValid();
   }

   @Override
   public Control createControl(Composite parent) {
      Group outputGroup = new Group(parent, SWT.NONE);
      outputGroup.setLayout(new GridLayout());
      outputGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      outputGroup.setText("Select UUT Output Options");

      this.consoleOutputPanel = new ServerOutputPanel(outputGroup, SWT.NONE);
      return outputGroup;
   }

   @Override
   public String getErrorMessage() {
      StringBuilder builder = new StringBuilder();
      if (consoleOutputPanel.areSettingsValid() != true) {
         if (builder.length() > 0) {
            builder.append(", ");
         }
         builder.append("UUT Output Redirect: ");
         builder.append(consoleOutputPanel.getErrorMessage());
      }
      return builder.toString();
   }

   @Override
   public int getPriority() {
      return 1;
   }

   @Override
   public void load(IPropertyStore propertyStore) {
      this.consoleOutputPanel.setSelected(propertyStore.get(StorageKeys.SERVER_OUTPUT_SELECTION_STORAGE_KEY));
      this.consoleOutputPanel.setFilePath(propertyStore.get(StorageKeys.SERVER_OUTPUT_FILE_PATH_STORAGE_KEY));
   }

   @Override
   public void save(IPropertyStore propertyStore) {
      propertyStore.put(StorageKeys.SERVER_OUTPUT_SELECTION_STORAGE_KEY, this.consoleOutputPanel.getSelected());
      propertyStore.put(StorageKeys.SERVER_OUTPUT_FILE_PATH_STORAGE_KEY, this.consoleOutputPanel.getFilePath());
   }

}

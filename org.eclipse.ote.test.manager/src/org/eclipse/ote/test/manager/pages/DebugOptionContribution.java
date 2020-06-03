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

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.ui.plugin.widgets.IPropertyStoreBasedControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

/**
 * @author Roberto E. Escobar
 */
public class DebugOptionContribution implements IPropertyStoreBasedControl {

   private final String GROUP_TITLE = "Debug Options";
   private final String LABEL_TEXT = "Debug UUT using TBD UUT IDE through Test Manager";
   private final String TOOLTIP_TEXT = "Select to allow UUT to connect in debug mode.";

   private Button uutDebugCheck;

   @Override
   public Control createControl(Composite parent) {
      Group debugGroup = new Group(parent, SWT.NONE);
      debugGroup.setLayout(new GridLayout());
      debugGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      debugGroup.setText(GROUP_TITLE);

      uutDebugCheck = new Button(debugGroup, SWT.CHECK);
      uutDebugCheck.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      uutDebugCheck.setText(LABEL_TEXT);
      uutDebugCheck.setToolTipText(TOOLTIP_TEXT);

      return debugGroup;
   }

   private void setDebugOption(boolean isEnabled) {
      if (uutDebugCheck != null && uutDebugCheck.isDisposed() != true) {
         uutDebugCheck.setSelection(isEnabled);
      }
   }

   public boolean isDebugOptionEnabled() {
      boolean toReturn = false;
      if (uutDebugCheck != null && uutDebugCheck.isDisposed() != true) {
         toReturn = uutDebugCheck.getSelection();
      }
      return toReturn;
   }

   @Override
   public void load(IPropertyStore propertyStore) {
      boolean isEnabled = propertyStore.getBoolean(StorageKeys.DEBUG_OPTIONS);
      setDebugOption(isEnabled);
   }

   @Override
   public void save(IPropertyStore propertyStore) {
      propertyStore.put(StorageKeys.DEBUG_OPTIONS, isDebugOptionEnabled());
   }

   @Override
   public boolean areSettingsValid() {
      return true;
   }

   @Override
   public String getErrorMessage() {
      return "";
   }

   @Override
   public int getPriority() {
      return 1;
   }

}
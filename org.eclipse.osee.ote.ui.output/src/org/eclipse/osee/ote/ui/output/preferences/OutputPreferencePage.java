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
package org.eclipse.osee.ote.ui.output.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ote.ui.output.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OutputPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

   public static final String LINES = "lines";
   public static final String TIME = "time";

   private Button time;
   private Button lines;

   public OutputPreferencePage() {
   }

   public OutputPreferencePage(String title) {
      super(title);
   }

   public OutputPreferencePage(String title, ImageDescriptor image) {
      super(title, image);
   }

   @Override
   protected Control createContents(Composite parent) {
      noDefaultAndApplyButton();
      time = new Button(parent, SWT.CHECK);
      time.setText("Show time on the details page.");
      time.setSelection(getPreferenceStore().getBoolean(TIME));
      time.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            // Intentionally Empty Block
         }

         @Override
         public void widgetSelected(SelectionEvent e) {
            getPreferenceStore().setValue(TIME, time.getSelection());
            System.out.println("time " + time.getSelection());
         }
      });

      lines = new Button(parent, SWT.CHECK);
      lines.setText("Show line numbers on the details page.");
      lines.setSelection(getPreferenceStore().getBoolean(LINES));
      lines.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            // Intentionally Empty Block
         }

         @Override
         public void widgetSelected(SelectionEvent e) {
            getPreferenceStore().setValue(LINES, lines.getSelection());
            System.out.println("lines " + lines.getSelection());
         }
      });

      return parent;
   }

   @Override
   public void init(IWorkbench workbench) {
      // Intentionally Empty Block
   }

   @Override
   protected IPreferenceStore doGetPreferenceStore() {
      return Activator.getDefault().getPreferenceStore();
   }
}

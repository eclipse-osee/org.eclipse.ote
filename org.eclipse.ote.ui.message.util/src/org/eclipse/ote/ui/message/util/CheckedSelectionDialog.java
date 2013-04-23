/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.message.util;

import java.util.Arrays;
import java.util.Map;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class CheckedSelectionDialog extends TrayDialog {

   private Map<String, Boolean> selections;
   private Table table;
   private String title;
   
   public CheckedSelectionDialog(Shell shell, String title, Map<String, Boolean> selections) {
      super(shell);
      this.title = title;
      this.selections = selections;
      setHelpAvailable(false);
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      Control dialogArea = super.createDialogArea(parent);
      table = new Table((Composite)dialogArea, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FILL);
      String[] filterArray = selections.keySet().toArray(new String[selections.keySet().size()]);
      Arrays.sort(filterArray);
      for (int i = 0; i < filterArray.length; i++) {
         final TableItem item = new TableItem(table, SWT.NONE);
         item.setText(filterArray[i]);
         item.setChecked(selections.get(filterArray[i]));
      }
      table.setLayoutData(new GridData(GridData.FILL_BOTH));
      getShell().setText(title);
      return dialogArea;
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginWidth = 11;
      layout.marginHeight = 7;
      layout.horizontalSpacing = 0;
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
      composite.setFont(parent.getFont());

      final Button selectAllButton = new Button(composite, SWT.CHECK);
      selectAllButton.setText("Select All");
      selectAllButton.setSelection(true);

      selectAllButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            for (TableItem item : table.getItems()) {

               if (selectAllButton.getSelection()) {
                  item.setChecked(true);
               } else {
                  item.setChecked(false);
               }
            }
         }
      });

     Control buttonSection = super.createButtonBar(composite);
     ((GridData) buttonSection.getLayoutData()).grabExcessHorizontalSpace = true;
     return composite;
  }
 
   public Map<String, Boolean> getFilters(){
      return selections;
   }

   @Override
   protected void okPressed() {
      for(TableItem item:table.getItems()){
         selections.put(item.getText(), item.getChecked());
      }
      super.okPressed();
   }
}

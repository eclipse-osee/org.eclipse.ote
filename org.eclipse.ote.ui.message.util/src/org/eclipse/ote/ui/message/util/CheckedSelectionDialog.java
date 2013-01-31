package org.eclipse.ote.ui.message.util;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class CheckedSelectionDialog extends TrayDialog {

   private Map<String, Boolean> selections;
   private Table table;
   
   public CheckedSelectionDialog(Shell shell, String title, Map<String, Boolean> selections) {
      super(shell);
      shell.setText(title);
      this.selections = selections;
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
      return dialogArea;
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
